package com.smart.cloud.smartplatformcommon.sftp.Transfer;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

public interface FileTransfer {
    /**
     * 移动文件
     *
     * @param oldpath
     * @param newpath
     * @throws Exception
     */
    public void rename(String oldpath, String newpath) throws Exception;

    /**
     * 删除文件
     *
     * @param path
     * @throws SftpException
     */
    public void delete(String path) throws Exception;

    /**
     * 获取文件修改时间(待测试)
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String lastModify(String path) throws Exception;

    /**
     * 下载
     *
     * @param path
     * @param saveFile
     * @return
     * @throws Exception
     */
    public boolean readFile(String path, File saveFile) throws Exception;

    /**
     * 获取文件流
     *
     * @param path
     * @return
     * @throws SftpException
     */
    public InputStream readFile(String path) throws Exception;

    /**
     * 创建文件夹
     *
     * @param path
     * @throws SftpException
     */
    public void mkdir(String path) throws Exception;

    /**
     * 文件获取文件名/文件夹则获取下面所有文件名
     *
     * @param path
     * @return
     * @throws SftpException
     */
    public List<String> allFileName(String path) throws Exception;

    public Vector<LsEntry> ls(String path) throws Exception;

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     * @throws SftpException
     */
    public long fileSzie(String path) throws Exception;

    /**
     * 文件上传
     *
     * @param file
     * @param path
     * @param fileName
     * @throws Exception
     * @throws SftpException
     */
    public void fileTransfer(File file, String path, String fileName) throws Exception;

    /**
     * 文件上传
     *
     * @param data
     * @param path
     * @param fileName
     * @throws Exception
     * @throws SftpException
     */
    public void fileTransfer(byte[] data, String path, String fileName) throws Exception;

    public SftpATTRS fileAttrs(String path) throws SftpException;
}
