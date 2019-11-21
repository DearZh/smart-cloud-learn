package com.smart.cloud.smartplatformcommon.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This is thread safe.
 * common.pool的池化工厂
 * arnold.zhao 2019/4/7
 */
public class SftpPoolableObjectFactory extends BasePoolableObjectFactory {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String host;
    private int port;
    private String user;
    private String password;
    /*
        假设当前有50台话机实时sftp链接传输，此处BlockingQueue队列将会阻塞等待可使用的回话；
        为了满足实时性SFTP传输的要求，此处初始化SFTP数量根据实际话机进行设置；取配置文件配置定义初始化回话数量
        arnold.zhao 2019/4/4
     */
    private int maxConnectInstance = 3;
    private BlockingQueue<Object> clientCacheQueue = new LinkedBlockingDeque<Object>();

    public SftpPoolableObjectFactory(String host, int port, String user,
                                     String password, Integer maxConnectInstance) {
//        BaseKeyedPoolableObjectFactory
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.maxConnectInstance = maxConnectInstance;
        createMoreObj();
    }

    /**
     * @Description: 链接池工厂初始化创建
     * @Author: Arnold.zhao
     * @Date: 2019/4/4
     */
    private void createMoreObj() {
        for (int i = 0; i < maxConnectInstance; i++) {
            try {
                // 创建JSch对象
                JSch jsch = new JSch();
                // 根据用户名，主机ip，端口获取一个Session对象
                Session session = jsch.getSession(user, host, port);
                if (password != null) {
                    // 设置密码
                    session.setPassword(password);
                }
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                // 为Session对象设置properties
                session.setConfig(config);
                // 设置timeout时间
                session.setTimeout(60000);
                // 通过Session建立链接
                session.connect();
                // 打开SFTP通道
                Channel chan = session.openChannel("sftp");
                // 建立SFTP通道的连接
                chan.connect();
                ChannelSftp channel = (ChannelSftp) chan;
                SftpClient sc = new SftpClient(session, channel);
                clientCacheQueue.add(sc);
                log.info("创建一个sftp连接{}:{}, 当前cacheSize:{}", host, port, clientCacheQueue.size());
            } catch (JSchException e) {
                log.error("sftp连接配置不正确,请检查参数配置. {}:{}", host, port);
                e.printStackTrace();
            }
        }
    }

    /**
     * The pool calls this method in sequence. So it creates objects one by one and all other threads have to wait - very slow!
     * So we use a object cache to speed up the obj creation.
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object makeObject() throws Exception {
        log.error("当前连接池中数量: {}", clientCacheQueue.size());
        /*
            FIXME
            由于此处使用了对象缓存来加快了对象的创建步骤，但由此带来的问题便是，如果当前上方初始化的连接池3个对象都在使用当中时，
            此时common.pool的连接池需再次创建对象，仍然是会把正在使用中的链接给出去，这将会导致不可避免的异常；
            换句话说则是：由于此处的链接创建为伪创建，所以实际此处的连接池是并不受GenericObjectPool.Config的部分参数影响的，除非我们适当的给予正确的配置；
            此处的设置将体现在，MAX_CONNECT_INSTANCE 参数上；
            arnold.zhao 2019/4/7
         */
        return clientCacheQueue.take();
    }

    @Override
    public void destroyObject(final Object obj) throws Exception {
        if (obj instanceof SftpClient) {
            doDestory((SftpClient) obj);
        }
    }

    private void doDestory(SftpClient obj) {
        SftpClient sc = obj;
        ChannelSftp channel = sc.getChannelSftp();
        Session session = sc.getSession();
        if (channel != null) {
            channel.quit();
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    @Override
    public boolean validateObject(Object obj) {
        if (obj instanceof SftpClient) {
            SftpClient sc = (SftpClient) obj;
            try {
                Vector ls = sc.getChannelSftp().ls("/");
                final boolean clientStatus = sc.getChannelSftp().isConnected()
                        && sc.getSession().isConnected()
                        && !sc.getChannelSftp().isClosed();
                return clientStatus;
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("sftp 连接校验失败....... {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

}
