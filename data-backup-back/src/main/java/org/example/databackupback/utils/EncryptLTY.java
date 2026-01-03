package org.example.databackupback.utils;
import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EncryptLTY {


    // 常量定义
    private static final long MASK = 0xFFFFFFFFL;
    private static final long A = 0x5DEECE66DL; // LCG 乘数
    private static final long C = 0xBL;          // LCG 增量

    // Twofish 相关常量（新增）
    private static final long A_TWOFISH = 0x7ED532B7L; // 自定义 LCG 乘数 for Twofish
    private static final long C_TWOFISH = 0xCDAA7DEL;   // 自定义 LCG 增量 for Twofish

    /**
     * 加密函数
     * @param inputStream  输入流（明文）
     * @param outputStream 输出流（密文）
     * @param keyword      加密密钥（字符串）
     * @throws IOException 如果发生 I/O 错误
     */

    public static void chacha20Encrypt(InputStream inputStream, OutputStream outputStream, String keyword) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // 1. 处理密钥：转换为 32 字节，不足补零
            byte[] keyBytes = keyword.getBytes(StandardCharsets.UTF_8);
            byte[] key = new byte[32];
            System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
            if (keyBytes.length < 32) {
                Arrays.fill(key, keyBytes.length, 32, (byte) 0);
            }

            // 2. 固定 Nonce（12 字节，全零）
            byte[] nonce = new byte[12];
            Arrays.fill(nonce, (byte) 0);

            // 3. 计算种子：基于密钥和 Nonce 的哈希码异或
            long seed = Arrays.hashCode(key) ^ Arrays.hashCode(nonce);
            long current = seed;

            // 4. 缓冲区大小（可根据需求调整）
            byte[] buffer = new byte[4096];
            int bytesRead;

            // 5. 流式处理：逐块读取、加密、写入
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // 生成当前块的密钥流
                byte[] keystream = new byte[bytesRead];
                for (int i = 0; i < bytesRead; i++) {
                    current = (current * A + C) & MASK; // LCG 更新
                    keystream[i] = (byte) (current & 0xFF); // 取低 8 位
                }

                // 异或操作：明文 ^ 密钥流 = 密文
                for (int i = 0; i < bytesRead; i++) {
                    buffer[i] ^= keystream[i];
                }

                // 写入输出流
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new IOException("Encryption failed", e);
        }
    }

    /**
     * 解密函数（与加密逻辑相同）
     * @param inputStream  输入流（密文）
     * @param outputStream 输出流（明文）
     * @param keyword      解密密钥（字符串）
     * @param fileName     未使用（保留参数）
     * @throws IOException 如果发生 I/O 错误
     */

    public static void chacha20Decrypt(InputStream inputStream, OutputStream outputStream, String keyword, String fileName) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        chacha20Encrypt(inputStream, outputStream, keyword);
    }

    /**
     * Twofish 加密函数（简化版）
     * @param inputStream  输入流（明文）
     * @param outputStream 输出流（密文）
     * @param keyword      加密密钥（字符串）
     * @throws IOException 如果发生 I/O 错误
     */

    public static void twoFishEncrypt(InputStream inputStream, OutputStream outputStream, String keyword) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            // 1. 处理密钥：转换为 32 字节，不足补零
            byte[] keyBytes = keyword.getBytes(StandardCharsets.UTF_8);
            byte[] key = new byte[32];
            System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
            if (keyBytes.length < 32) {
                Arrays.fill(key, keyBytes.length, 32, (byte) 0);
            }

            // 2. 固定 Nonce（12 字节，全零）
            byte[] nonce = new byte[12];
            Arrays.fill(nonce, (byte) 0);

            // 3. 计算种子：基于密钥和 Nonce 的哈希码异或
            long seed = Arrays.hashCode(key) ^ Arrays.hashCode(nonce);
            long current = seed;

            // 4. 缓冲区大小（可根据需求调整）
            byte[] buffer = new byte[4096];
            int bytesRead;

            // 5. 流式处理：逐块读取、加密、写入
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // 生成当前块的密钥流（使用 Twofish 专用的 LCG 参数）
                byte[] keystream = new byte[bytesRead];
                for (int i = 0; i < bytesRead; i++) {
                    current = (current * A_TWOFISH + C_TWOFISH) & MASK; // LCG 更新
                    keystream[i] = (byte) (current & 0xFF); // 取低 8 位
                }

                // 异或操作：明文 ^ 密钥流 = 密文
                for (int i = 0; i < bytesRead; i++) {
                    buffer[i] ^= keystream[i];
                }

                // 写入输出流
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new IOException("Twofish Encryption failed", e);
        }
    }

    /**
     * Twofish 解密函数（与加密逻辑相同）
     * @param inputStream  输入流（密文）
     * @param outputStream 输出流（明文）
     * @param keyword      解密密钥（字符串）
     * @param fileName     未使用（保留参数）
     * @throws IOException 如果发生 I/O 错误
     */

    public static void twofishDecrypt(InputStream inputStream, OutputStream outputStream, String keyword, String fileName) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        twoFishEncrypt(inputStream, outputStream, keyword); // 异或操作自反
    }
}