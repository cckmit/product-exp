package com.tmb.oneapp.productsexpservice.feignclients;


import com.jcraft.jsch.*;
import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.model.SFTPStoreFileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Provides method to store file to sftp server.
 */
@Service
public class SFTPClientImp implements FTPClient {

    @Value("${sftp.remote-host}")
    private String remoteHost;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.username}")
    private String username;
    @Value("${sftp.password}")
    private String password;

    private static final TMBLogger<SFTPClientImp> logger = new TMBLogger<>(SFTPClientImp.class);

    private static final String SEPARATOR = "/";


    private boolean isDirExist(ChannelSftp channel, String path) {
        try {
            channel.cd(path);
            return true;
        } catch (SftpException e) {
            logger.info("sftp directory: {} does not exist", path);
        }
        return false;
    }


    private boolean mkdir(ChannelSftp channel, String rootPath, String destDirs) {
        String currentPath = rootPath;
        try {
            String[] dirs = destDirs.split(SEPARATOR);
            String targetDir;
            for (String dir : dirs) {
                targetDir = currentPath + SEPARATOR + dir;
                if (!isDirExist(channel, targetDir)) {
                    channel.cd(currentPath);
                    channel.mkdir(dir);
                    logger.info("sftp directory: {} created", targetDir);
                }
                currentPath = targetDir;
            }
            return true;

        } catch (SftpException e) {
            logger.error("sftp exception:{}", e);
        }
        return false;
    }

    public Channel setupJsch() throws JSchException {
        JSch jsch = new JSch();
        Session jschSession = jsch.getSession(username, remoteHost);
        jschSession.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.setTimeout(60000);
        jschSession.connect();
        return jschSession.openChannel("sftp");
    }

    /**
     * Store file to sftp server
     *
     * @param storeFileInfoList
     * @return
     * @throws IOException
     */

    @Override
    public boolean storeFile(List<SFTPStoreFileInfo> storeFileInfoList) {
        ChannelSftp channelSftp = null;
        String dst;
        try {
            channelSftp = (ChannelSftp) setupJsch();
            channelSftp.connect();
            for (SFTPStoreFileInfo sftpStoreFileInfo : storeFileInfoList) {
                File sourceFile = new File(sftpStoreFileInfo.getSrcFile());
                if (!sourceFile.exists()) {
                    logger.error("src file to upload to ftp does not exists: {}", sftpStoreFileInfo.getSrcFile());
                    return false;
                }
                String dstDir = sftpStoreFileInfo.getDstDir();
                if (dstDir == null) {
                    dst = sftpStoreFileInfo.getRootPath() + SEPARATOR + sourceFile.getName();
                } else {
                    mkdir(channelSftp, sftpStoreFileInfo.getRootPath(), sftpStoreFileInfo.getDstDir());
                    dst = sftpStoreFileInfo.getRootPath() + SEPARATOR + sftpStoreFileInfo.getDstDir() + SEPARATOR + sourceFile.getName();
                }
                channelSftp.put(sftpStoreFileInfo.getSrcFile(), dst);
                logger.info("sftp stored success:{}", dst);
            }
            channelSftp.exit();
            return true;
        } catch (JSchException e) {
            logger.error("error sftp connection:{}", e);
        } catch (SftpException e) {
            logger.error("error sftp operation:{}", e);
        }
        return false;
    }

    public String getRemoteHost() {
        return remoteHost;
    }
}
