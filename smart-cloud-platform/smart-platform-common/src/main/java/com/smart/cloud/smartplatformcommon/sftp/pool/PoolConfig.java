package com.smart.cloud.smartplatformcommon.sftp.pool;

public class PoolConfig {
    private String host;
    private int port;
    private String user;
    private String password;
    private Integer maxConnectInstance;

    public PoolConfig(String host, int port, String user, String password, Integer maxConnectInstance) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.maxConnectInstance = maxConnectInstance;
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

    public Integer getMaxConnectInstance() {
        return maxConnectInstance;
    }

    public void setMaxConnectInstance(Integer maxConnectInstance) {
        this.maxConnectInstance = maxConnectInstance;
    }
}
