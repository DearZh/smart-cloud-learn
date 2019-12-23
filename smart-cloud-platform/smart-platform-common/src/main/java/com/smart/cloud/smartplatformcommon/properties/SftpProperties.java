package com.smart.cloud.smartplatformcommon.properties;

/**
 * Sftp上传时配置文件读取
 *
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2019-04-04
 */
public class SftpProperties {

    private String dirSource;
    private String host;
    private String port;
    private String username;
    private String password;
    private Integer maxActive;
    private Integer maxIdle;
    private Integer minIdle;
    private Integer maxConnectInstance;
    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getMaxConnectInstance() {
        return maxConnectInstance;
    }

    public void setMaxConnectInstance(Integer maxConnectInstance) {
        this.maxConnectInstance = maxConnectInstance;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDirSource() {
        return dirSource;
    }

    public void setDirSource(String dirSource) {
        this.dirSource = dirSource;
    }
}
