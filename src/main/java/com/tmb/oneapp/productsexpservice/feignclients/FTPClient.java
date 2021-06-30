package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.oneapp.productsexpservice.model.SFTPStoreFileInfo;

import java.io.IOException;
import java.util.List;

/**
 * Provides method to store file to ftp server.
 */
public interface FTPClient {
    /**
     * Store file to ftp server
     *
     * @param storeFileInfo
     * @return
     * @throws IOException
     */
    boolean storeFile(List<SFTPStoreFileInfo> storeFileInfo);
}
