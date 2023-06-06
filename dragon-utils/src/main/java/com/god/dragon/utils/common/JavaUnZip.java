package com.god.dragon.utils.common;


import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.utils.common
 * @date 2023/6/6 14:37
 * @Description java解压工具
 */
public class JavaUnZip {

    /**
     * zip解压缩
     *
     * @param zipFilePath 压缩文件
     * @param unZipFilePath 解压缩文件
     */
    public static void unZip(String zipFilePath, String unZipFilePath) {
        File zipFile = new File(zipFilePath);
        File upZipFile = new File(unZipFilePath);

        // 解压路径不存在,创建解压路径
        if (!upZipFile.exists() || !upZipFile.isDirectory()) {
            upZipFile.mkdirs();
        }

        try( ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));){
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryFilePath = unZipFilePath + File.separator + entry.getName();
                File entryFile = new File(entryFilePath);
                //判断当前文件父类路径是否存在，不存在则创建
                if (!entryFile.getParentFile().exists() || !entryFile.getParentFile().isDirectory()) {
                    entryFile.getParentFile().mkdirs();
                }
                //不是文件说明是文件夹创建即可，无需写入
                if (entry.isDirectory()) {
                    continue;
                }
                //文件拷贝,如果文件存在则进行覆盖
                Files.copy(zip.getInputStream(entry),entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * rar解压缩,rar目前是微软独有的压缩算法没有公开无法测试
     * @param zipFilePath
     * @param unZipFilePath
     */
    public static void unRar(String zipFilePath, String unZipFilePath) {
        File zipFile = new File(zipFilePath);
        File upZipFile = new File(unZipFilePath);

        // 解压路径不存在,创建解压路径
        if (!upZipFile.exists() || !upZipFile.isDirectory()) {
            upZipFile.mkdirs();
        }

        try(ArchiveInputStream archiveInputStream =
                    new ArchiveStreamFactory().createArchiveInputStream("rar",
                            new FileInputStream(zipFile))){
            ArchiveEntry entry;
            while ((entry = archiveInputStream.getNextEntry()) != null) {
                String entryFilePath = unZipFilePath + File.separator + entry.getName();
                File entryFile = new File(entryFilePath);
                //判断当前文件父类路径是否存在，不存在则创建
                if (!entryFile.getParentFile().exists() || !entryFile.getParentFile().isDirectory()) {
                    entryFile.getParentFile().mkdirs();
                }
                //不是文件说明是文件夹创建即可，无需写入
                if (entry.isDirectory()) {
                    continue;
                }
                //文件拷贝,如果文件存在则进行覆盖
                Files.copy(archiveInputStream,entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 7zip
     * @param zipFilePath
     * @param unZipFilePath
     */
    public static void un7zip(String zipFilePath, String unZipFilePath) {
        File zipFile = new File(zipFilePath);
        File upZipFile = new File(unZipFilePath);

        // 解压路径不存在,创建解压路径
        if (!upZipFile.exists() || !upZipFile.isDirectory()) {
            upZipFile.mkdirs();
        }

        try(SevenZFile sevenZFile = new SevenZFile(zipFile)){
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                String entryFilePath = unZipFilePath + File.separator + entry.getName();
                File entryFile = new File(entryFilePath);
                //判断当前文件父类路径是否存在，不存在则创建
                if (!entryFile.getParentFile().exists() || !entryFile.getParentFile().isDirectory()) {
                    entryFile.getParentFile().mkdirs();
                }
                //不是文件说明是文件夹创建即可，无需写入
                if (entry.isDirectory()) {
                    continue;
                }
                //文件拷贝,如果文件存在则进行覆盖
                Files.copy(sevenZFile.getInputStream(entry),entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {
        un7zip("C:\\Users\\64853\\Desktop\\abcd.7z","D:\\");
    }
}
