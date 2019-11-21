package com.smart.cloud.smartplatformcommon.sftp;

import com.smart.cloud.smartplatformcommon.properties.CommonProperties;
import com.smart.cloud.smartplatformcommon.sftp.pool.PoolConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 写入要同时写到两台服务器，两个线程同时写，必须同时成功，一台出错即为出错。 读取只从一台读取。只要有一台工作即可读取成功。 链接的取用和归还要正确。
 * TODO 多台服务器上的数据需要能根据数据库中的值同步，校对。（可用于新增服务器）
 */
@Component
public class SftpPoolManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String host;
    private String port;
    private String user;
    private String password;
    private int maxWait = 300 * 1000;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = true;

    @Autowired
    private CommonProperties commonProperties;

    /*
        This is thread safe.
        多sftp服务连接池配置使用，初始化add，只get不新增；
     */
    private List<SftpPool> sftpClientPoolList = new ArrayList<SftpPool>();
    private final static ReentrantReadWriteLock LIST_LOCKER = new ReentrantReadWriteLock();

    /**
     * 属性初始化，并调用连接池初始化方法
     */
    @PostConstruct
    public void init() {
        //统一采用Convert服务的上传方式，此处设置为不开启SFTP连接池的初始化操作
        if (commonProperties.getSftp().isFlag()) {
            host = commonProperties.getSftp().getHost();
            port = commonProperties.getSftp().getPort();
            user = commonProperties.getSftp().getUsername();
            password = commonProperties.getSftp().getPassword();
            Integer maxConnectInstance = commonProperties.getSftp().getMaxConnectInstance();

            if (StringUtils.isBlank(host) || StringUtils.isBlank(port) || StringUtils.isBlank(user)) {
                log.warn("sftp配置为空，初始化不获取sftp连接");
                return;
            }
            log.warn("开始初始化sftp连接");

            String[] hosts = host.split(",");
            String[] ports = port.split(",");
            String[] users = user.split(",");
            String[] passwords = password.split(",");
            if (hosts.length == ports.length && hosts.length == users.length && hosts.length == passwords.length) {
                List<PoolConfig> poolConfigList = new ArrayList<PoolConfig>();

                for (int i = 0; i < hosts.length; i++) {
                    PoolConfig pc = new PoolConfig(hosts[i], Integer.parseInt(ports[i]), users[i], passwords[i], maxConnectInstance);
                    poolConfigList.add(pc);
                }
                initPool(poolConfigList);
            } else {
                log.warn("连接参数配置错误 !!!");
            }
        }

    }

    public void destory() {
        for (SftpPool sp : sftpClientPoolList) {
            try {
                sp.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接池初始化.
     */
    private void initPool(List<PoolConfig> poolConfigList) {
        log.info("开始初始化sftp参数");

        GenericObjectPool.Config config = new Config();

        // 最大池容量
        config.maxActive = commonProperties.getSftp().getMaxActive() != null ? commonProperties.getSftp().getMaxActive() : 20;

        /*
            当前_pool空闲对象的集合大于_maxIdle所设置的最高空闲对象时，则新的空闲对象回收时直接destroy销毁掉；
            使用GenericObjectPool的工厂实例时，maxIdle默认值为8；此处maxIdle不配置时，原有的minIdle值大于8的配置则没有意义；
            arnold.zhao 2019/4/7

            连接池最大空闲
         */
        config.maxIdle = commonProperties.getSftp().getMaxActive() != null ? commonProperties.getSftp().getMaxIdle() : 10;

        //链接池最小空闲
        config.minIdle = commonProperties.getSftp().getMinIdle() != null ? commonProperties.getSftp().getMinIdle() : 8;
        // 从池中取对象达到最大时,继续创建新对象. //不启用，用默认值，链接满了等待
        // config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        // 池为空时取对象等待的最大毫秒数.
        config.maxWait = this.maxWait;
        // 取出对象时验证(此处设置成验证ftp是否处于连接状态).
        config.testOnBorrow = this.testOnBorrow;
        // 还回对象时验证(此处设置成验证ftp是否处于连接状态).
        config.testOnReturn = this.testOnReturn;
        try {
            for (PoolConfig poolConfig : poolConfigList) {
                SftpPool sp = new SftpPool(config, poolConfig.getHost(), poolConfig.getPort(), poolConfig.getUser(),
                        poolConfig.getPassword(), poolConfig.getMaxConnectInstance());
                /*
                    初始化各sftp服务模块连接池构建;
                    基于apache.common.pool对象池实现对应的sftp连接池，jcraft负责原有的sftp模块构建；
                    like arnold.zhao 2019/4/4
                 */
                sftpClientPoolList.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("sftp连接参数初始化结束");
    }

    /**
     * This is a blocking API. TODO consider to remove the synchronized
     * 如果客户有多个ftp。 随机获取Client 用于除了上传之外的一切方法。
     */
    public SftpClient getRandomClient() {
        int i = 0;
        List<SftpPool> alivePools = new ArrayList<SftpPool>();
        try {
            LIST_LOCKER.readLock().lock();
            alivePools.addAll(sftpClientPoolList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LIST_LOCKER.readLock().unlock();
        }
        SftpClient sc = null;
        SftpPool sftpPool = null;
        while (sc == null && alivePools.size() > 0) {
            try { // try another server
                sftpPool = alivePools.get(0);
                sc = sftpPool.borrowResource();
            } catch (Exception e) {
                alivePools.remove(0);
                e.printStackTrace();
            }
        }
        if (sc == null) {
            throw new RuntimeException("can not get sftp connection !!!");
        }
        logPoolStatus("get random resource in pool !!!", sftpPool);
        return sc;
    }

    private void logPoolStatus(String msg, SftpPool sftpPool) {
        log.debug("{} for host: {}:{};", msg, sftpPool.getHost(), sftpPool.getPort());
    }

    /**
     * FIXME MUST ENSURE the connections are alive, so we don't have to check it
     * at the upper level. 获取全部Client 用于上传到所有文件服务器,有连不上的就不传，直接扔异常 参数只用于区分两个同名方法
     * 随便传
     */
    public List<SftpClient> getClients() throws Exception {
        List<SftpClient> scList = new ArrayList<SftpClient>();
        try {
            LIST_LOCKER.readLock().lock();
            for (SftpPool sp : sftpClientPoolList) {
                logPoolStatus("before get random resource in pool !!!", sp);
                //SftpPoolableobjectFactory链接池构建后BlockingQueue取出对应你的SftpClient数据
                SftpClient sc = sp.borrowResource();
                logPoolStatus("after get random resource in pool !!!", sp);
                scList.add(sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LIST_LOCKER.readLock().unlock();
        }

        return scList;
    }

    /**
     * sftp连接完后释放链接
     */
    public void releaseConnection(List<SftpClient> scList) {
        if (scList == null || scList.size() <= 0) {
            return;
        }
        try {
            LIST_LOCKER.readLock().lock();
            for (int i = 0; i < sftpClientPoolList.size(); i++) {

                //FIXME 多sftp服务时,按照当前的取值顺序来看，是否会出现当前的SftpClient并非当前SftpPool链接池中对象的情况；待确定；arnold.zhao 2019/4/7
                //并不会，已确定，记录
                final SftpPool sftpPool = sftpClientPoolList.get(i);
                SftpClient sc = null;
                if (scList.size() >= i) { // avoid index out of bound
                    sc = scList.get(i);
                }
                logPoolStatus("before return random resource in pool !!!", sftpPool);
                removeOneConnection(sc, sftpPool);
                logPoolStatus("after return random resource in pool !!!", sftpPool);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LIST_LOCKER.readLock().unlock();
        }
    }

    /**
     * sftp连接完后释放链接
     */
    public void releaseConnection(SftpClient sc) {
        if (sc == null) {
            return;
        }
        try {
            LIST_LOCKER.readLock().lock();
            for (SftpPool aPool : sftpClientPoolList) {
                try {
                    removeOneConnection(sc, aPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LIST_LOCKER.readLock().unlock();
        }

    }

    private void removeOneConnection(SftpClient sc, SftpPool sftpPool) throws Exception {
        if (sc == null) {
            return;
        }
        if (sftpPool.getHost().equals(sc.getSession().getHost())) {
            sftpPool.returnResource(sc);
            logPoolStatus("return one connection", sftpPool);
        }
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public List<SftpPool> getSftpClientPoolList() {
        return sftpClientPoolList;
    }

    public void setSftpClientPoolList(List<SftpPool> sftpClientPoolList) {
        this.sftpClientPoolList = sftpClientPoolList;
    }

    public static ReentrantReadWriteLock getListLocker() {
        return LIST_LOCKER;
    }


}
