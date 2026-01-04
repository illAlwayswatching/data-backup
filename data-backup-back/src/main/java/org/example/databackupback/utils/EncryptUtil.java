package org.example.databackupback.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/12 18:28
 **/
public class EncryptUtil {
    // AES-128 加密算法配置
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // 使用CBC模式和PKCS5填充
    private static final int KEY_LENGTH = 16; // AES-128 需要 16 字节密钥
    private static final int IV_LENGTH = 16; // CBC 模式需要 16 字节 IV

    /**
     * 加密文件
     * @param inputStream 输入流（明文）
     * @param outputStream 输出流（密文）
     * @param keyword 加密密钥（16位数字字符串）
     * @throws Exception 加密过程中的异常
     */
    public static void encryptFile(InputStream inputStream, OutputStream outputStream, String keyword) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException, 
                   InvalidAlgorithmParameterException {
        // 1. 确保密钥长度为 16 字节（AES-128要求）
        byte[] keyBytes = keyword.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[KEY_LENGTH];
        
        if (keyBytes.length >= KEY_LENGTH) {
            // 如果密钥长度大于等于16字节，取前16字节
            System.arraycopy(keyBytes, 0, key, 0, KEY_LENGTH);
        } else {
            // 如果密钥长度小于16字节，复制现有字节，剩余部分补零
            System.arraycopy(keyBytes, 0, key, 0, keyBytes.length);
            // 剩余部分已经是0（byte数组默认值为0）
        }
        
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        
        // 2. 生成随机 IV（初始化向量）
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // 3. 初始化 Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        
        // 4. 先写入 IV（解密时需要读取）
        outputStream.write(iv);
        
        // 5. 加密数据
        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int i;
            while ((i = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, i);
            }
        }
    }

    /**
     * 解密文件
     * @param inputStream 输入流（密文）
     * @param outputStream 输出流（明文）
     * @param keyword 解密密钥（16位数字字符串）
     * @param fileName 文件名（保留参数，用于兼容性）
     * @throws Exception 解密过程中的异常
     */
    public static void decryptFile(InputStream inputStream, OutputStream outputStream, String keyword, String fileName) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, 
                   InvalidAlgorithmParameterException {
        // 1. 确保密钥长度为 16 字节（与加密时相同）
        byte[] keyBytes = keyword.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[KEY_LENGTH];
        
        if (keyBytes.length >= KEY_LENGTH) {
            System.arraycopy(keyBytes, 0, key, 0, KEY_LENGTH);
        } else {
            System.arraycopy(keyBytes, 0, key, 0, keyBytes.length);
            // 剩余部分已经是0
        }
        
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        
        // 2. 读取 IV（加密时写入的第一个16字节）
        byte[] iv = new byte[IV_LENGTH];
        int bytesRead = inputStream.read(iv);
        if (bytesRead != IV_LENGTH) {
            throw new IOException("无效的加密数据格式：无法读取IV（需要16字节）");
        }
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // 3. 初始化解密 Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        
        // 4. 直接解密到输出流，不需要临时文件
        try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int i;
            while ((i = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
                outputStream.flush();
            }
        }
    }
}
