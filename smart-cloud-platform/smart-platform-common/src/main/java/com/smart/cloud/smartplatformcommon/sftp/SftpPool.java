package com.smart.cloud.smartplatformcommon.sftp;


import com.smart.cloud.smartplatformcommon.sftp.pool.Pool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;


public class SftpPool extends Pool<SftpClient> {

    private String host;
    private int port;

    public SftpPool(Config poolConfig, String host, int port, String user, String password, Integer maxConnectInstance) {
        super(poolConfig, new SftpPoolableObjectFactory(host, port, user, password, maxConnectInstance));
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
