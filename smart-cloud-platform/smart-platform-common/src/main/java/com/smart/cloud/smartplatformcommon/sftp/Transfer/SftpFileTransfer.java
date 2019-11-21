package com.smart.cloud.smartplatformcommon.sftp.Transfer;

import com.smart.cloud.smartplatformcommon.converter.ConvertUtils;
import com.smart.cloud.smartplatformcommon.sftp.SftpClient;
import com.smart.cloud.smartplatformcommon.sftp.SftpPoolManager;
import com.smart.cloud.smartplatformcommon.sftp.pool.PoolException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Component
public class SftpFileTransfer implements FileTransfer {

    private static Log LOGGER = LogFactory.getLog(SftpFileTransfer.class);

    @Autowired
    public SftpPoolManager spm;

    public SftpFileTransfer() {

    }

    public SftpFileTransfer(SftpPoolManager spm) {
        this.spm = spm;
    }

    /**
     * 移动文件
     *
     * @throws SftpException
     * @throws PoolException
     */
    @Override
    public void rename(String oldpath, String newpath) throws SftpException, PoolException {
        oldpath = ConvertUtils.backslash(oldpath);
        newpath = ConvertUtils.backslash(newpath);
        List<SftpClient> scList = null;
        try {
            scList = spm.getClients();
            if (scList == null || scList.size() <= 0) {
                throw new PoolException("获取连接失败 !!!");
            } else {
                for (SftpClient sc : scList) {
                    ChannelSftp channel = sc.getChannelSftp();
                    channel.rename(oldpath, newpath);
                }
            }
        } catch (SftpException e) {
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
        } catch (PoolException e) {
            throw new PoolException("获取连接失败 !!!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            spm.releaseConnection(scList);
        }
    }

    /**
     * 删除文件
     *
     * @throws SftpException
     * @throws PoolException
     */
    @Override
    public void delete(String path) throws SftpException, PoolException {
        path = ConvertUtils.backslash(path);
        List<SftpClient> scList = null;
        try {
            scList = spm.getClients();
            if (scList == null || scList.size() <= 0) {
                throw new PoolException("获取连接失败 !!!");
            } else {
                for (SftpClient sc : scList) {
                    ChannelSftp channel = sc.getChannelSftp();
                    channel.rm(path);
                }
            }
        } catch (SftpException e) {
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
        } catch (PoolException e) {
            throw new PoolException("获取连接失败 !!!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            spm.releaseConnection(scList);
        }
    }

    /**
     * 获取文件修改时间(待测试)
     *
     * @throws SftpException
     */
    @Override
    public String lastModify(String path) throws SftpException {
        path = ConvertUtils.backslash(path);
        SftpATTRS lstat = this.fileAttrs(path);
        int mtime = lstat.getMTime();
        return mtime + "000";
    }

    /**
     * Read it all to memory TODO need to ensure the size will not blow the
     * memory 获取文件流
     *
     * @throws SftpException
     */
    @Override
    public boolean readFile(String path, File saveFile) throws SftpException {

        boolean flag = false;

        FileOutputStream os = null;

        path = ConvertUtils.backslash(path);
        SftpClient sc = spm.getRandomClient();
        ChannelSftp channel = sc.getChannelSftp();
        try {

            // 文件所在目录
            String dirpath = path.substring(0, path.lastIndexOf("/"));
            if (!StringUtils.endsWith(path, "/") && !StringUtils.endsWith(path, File.separator)) {

                /*if (SystemUtils.isLinux()) {
                    dirpath = dirpath + File.separator;
                } else {
                    dirpath = dirpath + "/";
                }*/
                //dirpath = dirpath + File.separator;
                dirpath = dirpath + "/";
            }
            channel.cd(dirpath);

            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }

            os = new FileOutputStream(saveFile);
            channel.get(path, os);
            flag = true;

            LOGGER.debug("___________附件:" + path + "下载成功");
        } catch (SftpException e) {
            LOGGER.debug("___________附件:" + path + "下载失败");
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
        } catch (Exception e) {
            LOGGER.warn("___________附件:" + path + "下载失败");
            throw new RuntimeException(e);
        } finally {
            spm.releaseConnection(sc);
            IOUtils.closeQuietly(os);
        }
        return flag;
    }

    /**
     * Read it all to memory TODO need to ensure the size will not blow the
     * memory 获取文件流
     *
     * @throws SftpException
     */
    @Override
    public InputStream readFile(String path) throws SftpException {
        path = ConvertUtils.backslash(path);
        SftpClient sc = spm.getRandomClient();
        ChannelSftp channel = sc.getChannelSftp();
        InputStream inputStream = null;
        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        try {
            inputStream = channel.get(path); // TODO can't we just return this
            // input stream to avoid putting
            // it all into the memory?
            bos = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, bos); // Read it all to memory
            bis = new ByteArrayInputStream(bos.toByteArray());
        } catch (SftpException e) {
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(bos);
            spm.releaseConnection(sc);
        }
        return bis;
    }

    /**
     * 创建文件夹
     *
     * @throws PoolException
     * @throws SftpException
     */
    @Override
    public void mkdir(String path) throws PoolException, SftpException {
        path = ConvertUtils.backslash(path);
        List<SftpClient> scList = null;
        try {

            LOGGER.debug("获取client==start");
            scList = spm.getClients();
            LOGGER.debug("获取client==end");
            if (scList == null || scList.size() <= 0) {
                throw new PoolException("获取连接失败 !!!");
            } else {
                for (SftpClient sc : scList) {
                    LOGGER.debug("path=" + path);
                    ChannelSftp channel = sc.getChannelSftp();
                    try {
                        LOGGER.debug("sftp===ls===start");
                        channel.ls(path);
                        LOGGER.debug("sftp===ls===end");
                    } catch (Exception e) {
                        LOGGER.debug("sftp===mkdir===start");
                        channel.mkdir(path);
                        LOGGER.debug("sftp===mkdir===end");
                    }
                }
            }
        } catch (SftpException e) {
            throw e;
        } catch (PoolException e) {
            throw new PoolException("获取连接失败 !!!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            spm.releaseConnection(scList);
        }
    }

    /**
     * 文件获取文件名/文件夹则获取下面所有文件名
     *
     * @throws SftpException
     */
    @Override
    public List<String> allFileName(String path) throws SftpException {
        path = ConvertUtils.backslash(path);
        List<String> list = new ArrayList<String>();
        Vector<LsEntry> v = this.ls(path);
        for (LsEntry lsEntry : v) {
            if (!".".equals(lsEntry.getFilename()) && !"..".equals(lsEntry.getFilename())) {
                list.add(lsEntry.getFilename());
            }
        }
        return list;
    }

    /**
     * 获取文件夹下所有文件信息
     */

    @Override
    public Vector<LsEntry> ls(String path) throws SftpException {
        path = ConvertUtils.backslash(path);
        SftpClient sc = spm.getRandomClient();
        ChannelSftp channel = sc.getChannelSftp();

        Vector<LsEntry> v = null;
        try {
            v = channel.ls(path);
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            spm.releaseConnection(sc);
        }
        return v;
    }

    /**
     * 获取文件大小
     *
     * @throws SftpException
     */
    @Override
    public long fileSzie(String path) throws SftpException {
        path = ConvertUtils.backslash(path);
        SftpATTRS lstat = this.fileAttrs(path);
        return lstat.getSize();
    }

    /**
     * 获取文件所有信息
     *
     * @throws SftpException
     */
    @Override
    public SftpATTRS fileAttrs(String path) throws SftpException {
        path = ConvertUtils.backslash(path);
        SftpClient sc = spm.getRandomClient();
        ChannelSftp channel = sc.getChannelSftp();
        SftpATTRS lstat = null;
        try {
            lstat = channel.lstat(path);
        } catch (SftpException e) {
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            spm.releaseConnection(sc);
        }
        return lstat;
    }

    /**
     * 文件上传
     *
     * @throws PoolException
     * @throws SftpException
     * @throws FileNotFoundException
     */
    @Override
    public void fileTransfer(File file, String path, String fileName) throws PoolException, SftpException, FileNotFoundException {
        path = ConvertUtils.backslash(path);
        List<SftpClient> scList = null;
        try {
            scList = spm.getClients();
            if (scList == null || scList.size() <= 0) {
                throw new PoolException("获取连接失败 !!!");
            } else {
                for (SftpClient sc : scList) {
                    ChannelSftp channel = sc.getChannelSftp();
                    try {
                        channel.ls(path);
                    } catch (Exception e) {
                        channel.mkdir(path);
                    }
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(file);
                        channel.put(fileInputStream, (path.endsWith("/") ? path : path + "/") + fileName, ChannelSftp.OVERWRITE);
                    } catch (SftpException e) {
                        throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
                    } catch (FileNotFoundException e) {
                        throw new FileNotFoundException(e.getMessage());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        IOUtils.closeQuietly(fileInputStream);
                    }
                }
            }
        } catch (SftpException e) {
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
        } catch (PoolException e) {
            throw new PoolException("获取连接失败 !!!");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            spm.releaseConnection(scList);
        }
    }

    /**
     * FIXME all data is in memory, we need to protect us!!!
     *
     * @param path TODO the path here can be any linux path if we use root. We
     *             must specify the parent path.
     */
    @Override
    public void fileTransfer(byte[] data, String path, String fileName) throws PoolException, SftpException {

        List<SftpClient> scList = null;

        try {
            scList = spm.getClients(); // blocking method
            if (scList == null || scList.size() <= 0) {
                throw new PoolException("获取连接失败 !!!");
            } else {
                for (SftpClient sc : scList) {
                    ChannelSftp channel = sc.getChannelSftp();
                    try {
                        channel.ls(path);
                    } catch (Exception e) {
                        channel.mkdir(path);
                    }
                    ByteArrayInputStream bais = null;
                    try {
                        bais = new ByteArrayInputStream(data); // TODO use
                        // buffer to
                        // speed up?
                        channel.put(bais, (path.endsWith("/") ? path : path + "/") + fileName, ChannelSftp.OVERWRITE);
                    } catch (SftpException e) {
                        throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "", e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        IOUtils.closeQuietly(bais);
                    }
                }
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (PoolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            spm.releaseConnection(scList);
        }
    }

    public SftpPoolManager getSpm() {
        return spm;
    }

    public void setSpm(SftpPoolManager spm) {
        this.spm = spm;
    }
}
