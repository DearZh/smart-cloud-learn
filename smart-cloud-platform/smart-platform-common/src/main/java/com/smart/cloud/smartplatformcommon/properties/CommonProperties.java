package com.smart.cloud.smartplatformcommon.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 全局配置读取
 *
 * @author Arnold.zhao <a href="mailto:13949123615@163.com"/>
 * @create 2019-04-04
 */
@Component
@ConfigurationProperties(prefix = "smart.psrt")
public class CommonProperties {

    private SftpProperties sftp = new SftpProperties();

    public SftpProperties getSftp() {
        return sftp;
    }

    public void setSftp(SftpProperties sftp) {
        this.sftp = sftp;
    }
}
