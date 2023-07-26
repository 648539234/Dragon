package com.god.dragon.common.utils;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Vector;

/**
 * SFTP工具
 *
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.pandora.infrastructure.utils
 * @date 2023/7/26 15:08
 * @description TODO
 */
public class SFtpClientService {
    private final static Logger log = LoggerFactory.getLogger(SFtpClientService.class);
    private final static int CONNECT_TIME_OUT = 10000; //超时时间单位毫秒

    /**
     * SFTP连接
     */
    public static ChannelSftp connect(String host, String port, String username, String password) {
        if (StringUtils.isBlank(host) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("SFTP请求参数不全");
        }
        Session session = null;
        ChannelSftp channel = null;
        try {
            JSch jsch = new JSch();
            if (StringUtils.isBlank(port)) { //默认连 22 端口
                session = jsch.getSession(username, host);
            } else {
                session = jsch.getSession(username, host, Integer.parseInt(host));
            }
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(CONNECT_TIME_OUT);

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(CONNECT_TIME_OUT);
            return channel;
        } catch (Exception e) {
            log.error("SFTP连接异常", e);
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * SFTP断开连接
     */
    public static void disconnect(ChannelSftp channel) {
        if (channel == null) {
            return;
        }
        try {
            if(channel.isConnected()) {
                channel.disconnect();
            }
            if(channel.getSession()!= null && channel.getSession().isConnected()) {
                channel.getSession().disconnect();
            }
        } catch (Exception e) {
            log.error("SFTP断开连接异常", e);
        }
    }

    /**
     * SFTP下载图片,remoteFilePath完整的文件路径
     */
    public static byte[] download(ChannelSftp channel, String remoteFilePath) {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP连接已断开");
        }
        if (!fileExist(channel, remoteFilePath)) {
            log.error("要下载的文件不存在,文件路径:{}", remoteFilePath);
            throw new RuntimeException( "系统异常,请联系管理员");
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(1024)) {
            channel.get(remoteFilePath, bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("SFTP下载异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * SFTP下载图片
     */
    public static byte[] download(ChannelSftp channel, String path, String remoteFileName) {
        String remoteFilePath;
        if (path.endsWith("/")) {
            remoteFilePath = path + remoteFileName;
        } else {
            remoteFilePath = path + "/" + remoteFileName;
        }
        return download(channel, remoteFilePath);
    }

    /**
     * SFTP下载图片,一次性
     */
    public static byte[] download(String host, String port, String username, String password, String remoteFilePath) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            return download(channel, remoteFilePath);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * SFTP下载图片,一次性
     */
    public static byte[] download(String host, String port, String username, String password, String path, String remoteFileName) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            return download(channel, path, remoteFileName);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * 上传图片
     */
    public static void upload(ChannelSftp channel, String path, String remoteFileName, byte[] content) {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP连接已断开");
        }
        if (!fileExist(channel, path)) { //如果文件路径不存在创建文件路径
            createDir(channel, path);
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(content)) {
            channel.cd(path);
            channel.put(bis, remoteFileName);
        } catch (Exception e) {
            log.error("SFTP上传异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * 上传图片
     */
    public static void upload(ChannelSftp channel, String path, String remoteFileName, InputStream is) {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP连接已断开");
        }
        if (!fileExist(channel, path)) { //如果文件路径不存在创建文件路径
            createDir(channel, path);
        }
        try (is) {
            channel.cd(path);
            channel.put(is, remoteFileName);
        } catch (Exception e) {
            log.error("SFTP上传异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * 上传图片
     */
    public static void upload(ChannelSftp channel, String remoteFilePath, byte[] content) {
        String path = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
        String remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
        upload(channel, path, remoteFileName, content);
    }

    /**
     * 上传图片
     */
    public static void upload(ChannelSftp channel, String remoteFilePath, InputStream is) {
        String path = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
        String remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
        upload(channel, path, remoteFileName, is);
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String remoteFilePath, byte[] content) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            upload(channel, remoteFilePath, content);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String remoteFilePath, InputStream is) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            upload(channel, remoteFilePath, is);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String path, String remoteFileName, InputStream is) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            upload(channel, path, remoteFileName, is);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * 上传图片,一次性
     */
    public static void upload(String host, String port, String username, String password, String path, String remoteFileName, byte[] content) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            upload(channel, path, remoteFileName, content);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * 删除文件(可删除文件夹)
     */
    public static void delete(ChannelSftp channel, String remoteFilePath) {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP连接已断开");
        }
        if (!fileExist(channel, remoteFilePath)) {
            log.info("要删除的文件不存在,文件路径:{}", remoteFilePath);
            return;
        }
        try {
            deleteAllDir(channel, remoteFilePath);
        } catch (Exception e) {
            log.error("删除文件夹失败,文件路径:{}", remoteFilePath);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * 删除文件(可删除文件夹)
     */
    public static void delete(ChannelSftp channel, String path, String remoteFileName) {
        String remoteFilePath;
        if (path.endsWith("/")) {
            remoteFilePath = path + remoteFileName;
        } else {
            remoteFilePath = path + "/" + remoteFileName;
        }
        delete(channel, remoteFilePath);
    }

    /**
     * 删除文件(可删除文件夹) 一次性
     */
    public static void delete(String host, String port, String username, String password, String remoteFilePath) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            delete(channel, remoteFilePath);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * 删除文件(可删除文件夹) 一次性
     */
    public static void delete(String host, String port, String username, String password, String path, String remoteFileName) {
        ChannelSftp channel = connect(host, port, username, password);
        try {
            delete(channel, path, remoteFileName);
        } finally {
            disconnect(channel);
        }
    }

    /**
     * 创建文件路径
     *
     * @param channel
     * @param createPath
     */
    public static void createDir(ChannelSftp channel, String createPath) {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP连接已断开");
        }
        String[] pathArray = createPath.split("/");
        StringBuilder filePath = new StringBuilder("/");
        try {
            for (String path : pathArray) {
                if ("".equals(path)) {
                    continue;
                }
                filePath.append(path + "/");
                if (!fileExist(channel, filePath.toString())) {
                    channel.mkdir(filePath.toString());
                }
            }
        } catch (Exception e) {
            log.error("创建文件夹失败,文件夹路径:{}", filePath, e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * SFTP 判断路径是否存在,filePath可以是文件夹路径也可以是文件路径
     */
    public static boolean fileExist(ChannelSftp channel, String filePath) {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP连接已断开");
        }
        try {
            channel.lstat(filePath);
        } catch (SftpException e) {
            if ("No such file".equals(e.getMessage())) {
                return false;
            }
            log.error("SFTP连接异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
        return true;
    }

    /**
     * SFTP 判断是否是文件夹,filePath可以是文件夹路径也可以是文件路径
     */
    public static boolean fileIsDir(ChannelSftp channel, String filePath) {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP连接已断开");
        }
        try {
            SftpATTRS lstat = channel.lstat(filePath);
            if (lstat.isDir()) {
                return true;
            } else {
                return false;
            }
        } catch (SftpException e) {
            if ("No such file".equals(e.getMessage())) {
                throw new RuntimeException( "文件路径不存在");
            }
            log.error("SFTP连接异常", e);
            throw new RuntimeException( "系统异常,请联系管理员");
        }
    }

    /**
     * 用于递归删除文件
     */
    private static void deleteAllDir(ChannelSftp channel, String dirName) throws SftpException {
        if (fileIsDir(channel, dirName)) { //如果是文件夹进行递归删除
            channel.cd(dirName);
            Vector<ChannelSftp.LsEntry> fileNames = channel.ls(".");
            for (ChannelSftp.LsEntry file : fileNames) {
                if (".".equals(file.getFilename()) || "..".equals(file.getFilename())) {
                    continue;
                }
                deleteAllDir(channel, file.getFilename());
            }
            channel.cd("..");
            channel.rmdir(dirName);
        } else {
            channel.rm(dirName);
        }
    }
}
