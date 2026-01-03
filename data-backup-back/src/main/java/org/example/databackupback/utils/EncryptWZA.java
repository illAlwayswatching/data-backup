package org.example.databackupback.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

// 导入BouncyCastle相关类
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EncryptWZA {
    // 加密算法参数 - 公共配置
    private static final int KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 10000;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16; // 大多数块加密算法使用16字节IV
    
    // 算法常量定义
    public static final String SERPENT = "Serpent";
    public static final String CAMELLIA = "Camellia";
    
    // 静态代码块：注册BouncyCastle加密提供者
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 使用指定算法加密数据
     * @param algorithm 加密算法 (SERPENT 或 CAMELLIA)
     * @param inputStream 输入流（明文）
     * @param outputStream 输出流（密文）
     * @param password 加密密码
     */
    public static void encrypt(String algorithm, InputStream inputStream, 
                              OutputStream outputStream, String password) throws Exception {
        // 生成随机盐和IV
        byte[] salt = generateRandomBytes(SALT_LENGTH);
        byte[] iv = generateRandomBytes(IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // 从密码生成密钥
        SecretKey key = generateKey(password, salt, algorithm);
        
        // 先写入盐和IV，解密时需要
        outputStream.write(salt);
        outputStream.write(iv);
        
        // 初始化加密器，指定使用BouncyCastle提供者
        String transformation = algorithm + "/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        // 加密并写入
        try (CipherOutputStream cipherOut = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 使用指定算法解密数据
     * @param algorithm 解密算法 (SERPENT 或 CAMELLIA)
     * @param inputStream 输入流（密文）
     * @param outputStream 输出流（明文）
     * @param password 解密密码
     */
    public static void decrypt(String algorithm, InputStream inputStream, 
                              OutputStream outputStream, String password, String fileName) throws Exception {
        // 读取盐和IV
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        
        if (inputStream.read(salt) != SALT_LENGTH || inputStream.read(iv) != IV_LENGTH) {
            throw new IOException("无效的加密数据格式");
        }
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // 生成密钥
        SecretKey key = generateKey(password, salt, algorithm);
        
        // 初始化解密器，指定使用BouncyCastle提供者
        String transformation = algorithm + "/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        // 解密并写入
        try (CipherInputStream cipherIn = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cipherIn.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 字符串加密便捷方法
     */
    public static String encryptString(String algorithm, String plaintext, String password) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            encrypt(algorithm, in, out, password);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        }
    }

    /**
     * 字符串解密便捷方法
     */
    public static String decryptString(String algorithm, String ciphertext, String password) throws Exception {
        byte[] data = Base64.getDecoder().decode(ciphertext);
        try (ByteArrayInputStream in = new ByteArrayInputStream(data);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            decrypt(algorithm, in, out, password, "defaultFileName");
            return new String(out.toByteArray());
        }
    }

    /**
     * 生成随机字节数组
     */
    private static byte[] generateRandomBytes(int length) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[length];
        java.security.SecureRandom random = java.security.SecureRandom.getInstanceStrong();
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * 从密码和盐生成指定算法的密钥
     */
    private static SecretKey generateKey(String password, byte[] salt, String algorithm) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 使用PBKDF2进行密钥派生
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, algorithm);
    }

    // 测试方法
    public static void main(String[] args) {
        try {
            String testData = "这是一个测试字符串，用于验证Serpent和Camellia算法的实现";
            String password = "testPassword123!";
            
            // 测试Serpent算法
            System.out.println("=== 测试 Serpent 算法 ===");
            String serpentEncrypted = encryptString(SERPENT, testData, password);
            System.out.println("加密后: " + serpentEncrypted);
            
            String serpentDecrypted = decryptString(SERPENT, serpentEncrypted, password);
            System.out.println("解密后: " + serpentDecrypted);
            System.out.println("验证结果: " + testData.equals(serpentDecrypted) + "\n");
            
            // 测试Camellia算法
            System.out.println("=== 测试 Camellia 算法 ===");
            String camelliaEncrypted = encryptString(CAMELLIA, testData, password);
            System.out.println("加密后: " + camelliaEncrypted);
            
            String camelliaDecrypted = decryptString(CAMELLIA, camelliaEncrypted, password);
            System.out.println("解密后: " + camelliaDecrypted);
            System.out.println("验证结果: " + testData.equals(camelliaDecrypted));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



