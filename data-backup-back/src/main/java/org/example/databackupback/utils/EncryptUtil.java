package org.example.databackupback.utils;

import org.example.databackupback.common.Response;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/12 18:28
 **/
public class EncryptUtil {
    // 文件的加密方式
    private static final String ALGORITHM = "AES";

    public static void encryptFile(InputStream inputStream, OutputStream outputStream, String keyword) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        // 使用密钥字符串生成秘密密钥
        SecretKey secretKeySpec = new SecretKeySpec(keyword.getBytes(), ALGORITHM);
        // 获取 AES 加密算法的实例
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 使用秘密密钥初始化密码 cipher，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            // 创建密码输出流，连接到输出流，并使用密码 cipher 进行加密
            // 缓冲区大小
            byte[] buffer = new byte[4096];
            int i;
            // 读取源文件内容到缓冲区
            while ((i = inputStream.read(buffer)) != -1) {
                // 将加密后的数据写入加密文件
                cipherOutputStream.write(buffer, 0, i);
            }
        }
    }

    public static void decryptFile(InputStream inputStream, OutputStream outputStream, String keyword, String fileName) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        SecretKey secretKeySpec = new SecretKeySpec(keyword.getBytes(), ALGORITHM); // 使用密钥字符串生成秘密密钥
        Cipher cipher = Cipher.getInstance(ALGORITHM); // 获取 AES 加密算法的实例
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec); // 使用秘密密钥初始化密码 cipher，设置为解密模式

        //////////////
        String temp_path = Response.PROJECT_PATH + "/temp/" + fileName;
        Path tempPath = Paths.get(temp_path);
        OutputStream tempStream = Files.newOutputStream(tempPath);

        try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) { // 创建密码输入流，连接到输入流，并使用密码 cipher 进行解密
            byte[] buffer = new byte[4096]; // 缓冲区大小
            int i;
            while ((i = cipherInputStream.read(buffer)) != -1) { // 读取加密文件内容到缓冲区
                tempStream.write(buffer, 0, i); // 将解密后的数据写入解密文件
                tempStream.flush();
            }
        }

        InputStream is = Files.newInputStream(tempPath);
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            byte[] buffer = new byte[4096]; // 缓冲区大小
            int i;
            while ((i = bis.read(buffer)) != -1) { // 读取加密文件内容到缓冲区
                outputStream.write(buffer, 0, i); // 将解密后的数据写入解密文件
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File tempFile = new File(temp_path);
        tempFile.delete();
    }
}
