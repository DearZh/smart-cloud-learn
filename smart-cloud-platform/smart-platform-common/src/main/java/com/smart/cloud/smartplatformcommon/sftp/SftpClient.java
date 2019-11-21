package com.smart.cloud.smartplatformcommon.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;


public class SftpClient {

    private Session session;

    private ChannelSftp channelSftp;

    public SftpClient(Session session, ChannelSftp channelSftp) {
        this.session = session;
        this.channelSftp = channelSftp;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public ChannelSftp getChannelSftp() {
        return channelSftp;
    }

    public void setChannelSftp(ChannelSftp channelSftp) {
        this.channelSftp = channelSftp;
    }


}
