package com.god.dragon.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * FTP工具,目前只实现上传和下载功能,其他功能待完善
 *
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.pandora.infrastructure.utils
 * @date 2023/7/26 15:08
 * @description TODO
 */
public class FtpClientService {
    private final static Logger log = LoggerFactory.getLogger(FtpClientService.class);
    private final static int CONNECT_TIME_OUT = 10000; //超时时间单位毫秒

    /**
     * FTP连接
     */
    public static FTPClient connect(String host, String port, String username, String password) {
        if (StringUtils.isBlank(host) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("FTP请求参数不全");
        }
        boolean loginFlag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            if (StringUtils.isBlank(port)){ //默认连 21 端口
                ftpClient.connect(host);
            }else {
                ftpClient.connect(host, Integer.parseInt(port));
            }
            if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
                throw new RuntimeException("尝试连接FTP失败,访问域名:"+host+",端口:"+(StringUtils.isBlank(port)?"21":port));
            }
            if (!ftpClient.login(username, password)) {
                throw new RuntimeException("登录FTP的用户名或密码错误,用户名:"+username+",密码:"+password);
            }else{
                loginFlag = true;
            }
            ftpClient.enterLocalPassiveMode(); //采用被动模式
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); //采用二进制文件类型
            return ftpClient;
        } catch (Exception e) {
            log.error("FTP连接异常", e);
            try {
                if (loginFlag) { //如果FTP已登录先退出登录
                    ftpClient.logout();
                }
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            }catch (Exception e2){
                log.error("FTP断开连接异常",e2);
            }
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * FTP断开连接
     */
    public static void disconnect(FTPClient ftpClient) {
        if (ftpClient == null) {
            return;
        }
        try {
            if(ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            log.error("FTP断开连接异常", e);
        }
    }

    /**
     * FTP下载图片,remoteFilePath完整的文件路径
     */
    public static byte[] download(FTPClient client, String remoteFilePath) {
        String path = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
        String remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
        return download(client, path, remoteFileName);
    }

    /**
     * FTP下载图片
     */
    public static byte[] download(FTPClient client, String path, String remoteFileName) {
        if (client == null || !client.isConnected()) {
            throw new IllegalArgumentException("FTP连接已断开");
        }
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream(1024)){
            if(client.changeWorkingDirectory(path)){
                throw new RuntimeException("切换路径异常,路径:"+path);
            }
            client.retrieveFile(remoteFileName, bos);
            return bos.toByteArray();
        }catch (Exception e){
            log.error("SFTP下载异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * FTP下载图片,一次性
     */
    public static byte[] download(String host, String port, String username, String password, String remoteFilePath) {
        FTPClient client = connect(host, port, username, password);
        try {
            return download(client, remoteFilePath);
        } finally {
            disconnect(client);
        }
    }

    /**
     * FTP下载图片,一次性
     */
    public static byte[] download(String host, String port, String username, String password, String path, String remoteFileName) {
        FTPClient client = connect(host, port, username, password);
        try {
            return download(client, path, remoteFileName);
        } finally {
            disconnect(client);
        }
    }

    /**
     * 上传图片
     */
    public static void upload(FTPClient client, String path, String remoteFileName, byte[] content) {
        if (client == null || !client.isConnected()) {
            throw new IllegalArgumentException("FTP连接已断开");
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(content)) {
            if(client.changeWorkingDirectory(path)){
                throw new RuntimeException("切换路径异常,路径:"+path);
            }
            client.storeFile(remoteFileName,bis);
        } catch (Exception e) {
            log.error("FTP上传异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * 上传图片
     */
    public static void upload(FTPClient client, String path, String remoteFileName, InputStream is) {
        if (client == null || !client.isConnected()) {
            throw new IllegalArgumentException("FTP连接已断开");
        }

        try(is){
            if(client.changeWorkingDirectory(path)){
                throw new RuntimeException("切换路径异常,路径:"+path);
            }
            client.storeFile(remoteFileName,is);
        } catch (Exception e) {
            log.error("FTP上传异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * 上传图片
     */
    public static void upload(FTPClient client, String remoteFilePath, byte[] content) {
        String path = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
        String remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
        upload(client, path, remoteFileName, content);
    }

    /**
     * 上传图片
     */
    public static void upload(FTPClient client, String remoteFilePath, InputStream is) {
        String path = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
        String remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
        upload(client, path, remoteFileName, is);
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String remoteFilePath, byte[] content) {
        FTPClient client = connect(host, port, username, password);
        try {
            upload(client, remoteFilePath, content);
        } finally {
            disconnect(client);
        }
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String remoteFilePath, InputStream is) {
        FTPClient client = connect(host, port, username, password);
        try {
            upload(client, remoteFilePath, is);
        } finally {
            disconnect(client);
        }
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String path, String remoteFileName, InputStream is) {
        FTPClient client = connect(host, port, username, password);
        try {
            upload(client, path, remoteFileName, is);
        } finally {
            disconnect(client);
        }
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String path, String remoteFileName, byte[] content) {
        FTPClient client = connect(host, port, username, password);
        try {
            upload(client, path, remoteFileName, content);
        } finally {
            disconnect(client);
        }
    }
}
