package org.example.databackupback.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/12 18:28
 **/
public class EncryptUtil {
    private static final int BLOCK_SIZE = 16;
    private static final int KEY_LENGTH = 16;
    private static final int IV_LENGTH = 16;
    private static final int ROUNDS = 10;
    
    // 程序生成的S-box和INV_S_BOX
    private static final int[] S_BOX = generateSBox();
    private static final int[] INV_S_BOX = generateInvSBox(S_BOX);
    private static final int[] RCON = generateRcon();
    
    /**
     * GF(2^8)上的乘法
     */
    private static int gfMultiply(int a, int b) {
        int result = 0;
        int temp = a & 0xff;
        int poly = 0x11b; // 不可约多项式 x^8 + x^4 + x^3 + x + 1
        
        while (b != 0) {
            if ((b & 1) != 0) {
                result ^= temp;
            }
            temp <<= 1;
            if ((temp & 0x100) != 0) {
                temp ^= poly;
            }
            b >>= 1;
        }
        return result & 0xff;
    }
    
    /**
     * 在GF(2^8)上计算乘法逆元
     * 使用穷举法：对于每个值，找到它的逆元
     */
    private static int gfMulInverse(int a) {
        if (a == 0) return 0;
        
        // 对于每个可能的逆元候选值，检查是否满足 a * inv = 1
        for (int inv = 1; inv < 256; inv++) {
            if (gfMultiply(a, inv) == 1) {
                return inv;
            }
        }
        return 0; // 不应该到达这里
    }
    
    /**
     * 仿射变换 (AES标准)
     * y = Ax + b，其中A是8x8矩阵，b是常数向量0x63
     */
    private static int affineTransform(int x) {
        // AES仿射变换矩阵（按列）
        int[] matrix = {0xf1, 0xe3, 0xc7, 0x8f, 0x1f, 0x3e, 0x7c, 0xf8};
        int constant = 0x63;
        int result = constant;
        
        for (int i = 0; i < 8; i++) {
            if ((x & (1 << i)) != 0) {
                result ^= matrix[i];
            }
        }
        return result & 0xff;
    }
    
    /**
     * 生成S-box
     */
    private static int[] generateSBox() {
        int[] sbox = new int[256];
        sbox[0] = 0x63; // 0的特殊情况
        for (int i = 1; i < 256; i++) {
            int inv = gfMulInverse(i);
            sbox[i] = affineTransform(inv);
        }
        return sbox;
    }
    
    /**
     * 生成逆S-box
     */
    private static int[] generateInvSBox(int[] sbox) {
        int[] invSbox = new int[256];
        for (int i = 0; i < 256; i++) {
            // 找到S-box中值为i的位置
            for (int j = 0; j < 256; j++) {
                if (sbox[j] == i) {
                    invSbox[i] = j;
                    break;
                }
            }
        }
        return invSbox;
    }
    
    /**
     * 生成轮常量RCON
     */
    private static int[] generateRcon() {
        int[] rcon = new int[11];
        rcon[0] = 0x00;
        rcon[1] = 0x01;
        for (int i = 2; i < 11; i++) {
            rcon[i] = rcon[i - 1] << 1;
            if (rcon[i] > 0xff) {
                rcon[i] ^= 0x11b; // 模不可约多项式
            }
            rcon[i] &= 0xff;
        }
        return rcon;
    }
    
    /**
     * 基于keyword生成确定性的16字节密钥
     * 使用简单的哈希算法确保相同keyword生成相同密钥
     */
    private static byte[] generateKey(String keyword) {
        byte[] keyBytes = keyword.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[KEY_LENGTH];
        
        // 使用简单的哈希算法生成确定性密钥
        long hash = 0;
        for (byte b : keyBytes) {
            hash = hash * 31 + (b & 0xff);
        }
        
        // 填充密钥数组
        for (int i = 0; i < KEY_LENGTH; i++) {
            if (i < keyBytes.length) {
                key[i] = keyBytes[i];
            } else {
                // 使用哈希值填充剩余字节
                hash = hash * 1103515245 + 12345;
                key[i] = (byte) (hash & 0xff);
            }
        }
        
        // 进一步混合，确保密钥分布均匀
        for (int round = 0; round < 4; round++) {
            for (int i = 0; i < KEY_LENGTH; i++) {
                int j = (i + 1) % KEY_LENGTH;
                key[i] = (byte) ((key[i] ^ key[j] ^ (hash >> (i * 8))) & 0xff);
            }
        }
        
        return key;
    }
    
    // 密钥扩展 - 正确的AES-128密钥扩展算法
    private static class KeySchedule {
        private final byte[][] roundKeys;
        
        KeySchedule(byte[] key) {
            this.roundKeys = new byte[ROUNDS + 1][];
            expandKey(key);
        }
        
        private void expandKey(byte[] key) {
            // AES-128需要44个字（11轮 × 4字/轮）
            int[] w = new int[44];
            
            // 前4个字直接来自密钥
            for (int i = 0; i < 4; i++) {
                w[i] = ((key[i * 4] & 0xff) << 24) |
                       ((key[i * 4 + 1] & 0xff) << 16) |
                       ((key[i * 4 + 2] & 0xff) << 8) |
                       (key[i * 4 + 3] & 0xff);
            }
            
            // 扩展剩余的字
            for (int i = 4; i < 44; i++) {
                int temp = w[i - 1];
                if (i % 4 == 0) {
                    // RotWord
                    temp = ((temp << 8) | (temp >>> 24)) & 0xffffffff;
                    // SubWord
                    temp = (S_BOX[(temp >>> 24) & 0xff] << 24) |
                           (S_BOX[(temp >>> 16) & 0xff] << 16) |
                           (S_BOX[(temp >>> 8) & 0xff] << 8) |
                           S_BOX[temp & 0xff];
                    // XOR Rcon
                    temp ^= (RCON[i / 4] << 24);
                }
                w[i] = w[i - 4] ^ temp;
            }
            
            // 转换为字节数组
            for (int round = 0; round <= ROUNDS; round++) {
                roundKeys[round] = new byte[16];
                for (int i = 0; i < 4; i++) {
                    int word = w[round * 4 + i];
                    roundKeys[round][i * 4] = (byte) (word >>> 24);
                    roundKeys[round][i * 4 + 1] = (byte) (word >>> 16);
                    roundKeys[round][i * 4 + 2] = (byte) (word >>> 8);
                    roundKeys[round][i * 4 + 3] = (byte) word;
                }
            }
        }
        
        byte[] getRoundKey(int round) {
            return roundKeys[round];
        }
    }
    
    // SubBytes
    private static void subBytes(byte[] state) {
        for (int i = 0; i < 16; i++) {
            state[i] = (byte) S_BOX[state[i] & 0xff];
        }
    }
    
    // InvSubBytes
    private static void invSubBytes(byte[] state) {
        for (int i = 0; i < 16; i++) {
            state[i] = (byte) INV_S_BOX[state[i] & 0xff];
        }
    }
    
    /**
     * 计算ShiftRows的索引映射
     * AES state按列存储: [0,4,8,12, 1,5,9,13, 2,6,10,14, 3,7,11,15]
     * 对于索引i: row = i % 4, col = i / 4
     * ShiftRows: 每行左移row个位置
     */
    private static int calculateShiftedIndex(int index, int shift) {
        int row = index % 4;
        int col = index / 4;
        int newCol = (col + shift) % 4;
        return row + newCol * 4;
    }
    
    // ShiftRows - 使用计算的索引
    private static void shiftRows(byte[] state) {
        byte[] temp = new byte[16];
        System.arraycopy(state, 0, temp, 0, 16);
        
        for (int i = 0; i < 16; i++) {
            int row = i % 4;
            int shift = row; // Row 0: 0位, Row 1: 1位, Row 2: 2位, Row 3: 3位
            int newIndex = calculateShiftedIndex(i, shift);
            state[newIndex] = temp[i];
        }
    }
    
    // InvShiftRows - 使用计算的索引
    private static void invShiftRows(byte[] state) {
        byte[] temp = new byte[16];
        System.arraycopy(state, 0, temp, 0, 16);
        
        for (int i = 0; i < 16; i++) {
            int row = i % 4;
            int shift = (4 - row) % 4; // 反向移位: Row 0: 0位, Row 1: 3位, Row 2: 2位, Row 3: 1位
            int newIndex = calculateShiftedIndex(i, shift);
            state[newIndex] = temp[i];
        }
    }
    
    // MixColumns (使用查找表优化)
    private static int gmul(int a, int b) {
        int p = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) p ^= a;
            boolean hi = (a & 0x80) != 0;
            a <<= 1;
            if (hi) a ^= 0x1b;
            b >>= 1;
        }
        return p & 0xff;
    }
    
    private static void mixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int s0 = state[c * 4] & 0xff;
            int s1 = state[c * 4 + 1] & 0xff;
            int s2 = state[c * 4 + 2] & 0xff;
            int s3 = state[c * 4 + 3] & 0xff;
            
            state[c * 4] = (byte) (gmul(2, s0) ^ gmul(3, s1) ^ s2 ^ s3);
            state[c * 4 + 1] = (byte) (s0 ^ gmul(2, s1) ^ gmul(3, s2) ^ s3);
            state[c * 4 + 2] = (byte) (s0 ^ s1 ^ gmul(2, s2) ^ gmul(3, s3));
            state[c * 4 + 3] = (byte) (gmul(3, s0) ^ s1 ^ s2 ^ gmul(2, s3));
        }
    }
    
    private static void invMixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int s0 = state[c * 4] & 0xff;
            int s1 = state[c * 4 + 1] & 0xff;
            int s2 = state[c * 4 + 2] & 0xff;
            int s3 = state[c * 4 + 3] & 0xff;
            
            state[c * 4] = (byte) (gmul(0x0e, s0) ^ gmul(0x0b, s1) ^ gmul(0x0d, s2) ^ gmul(0x09, s3));
            state[c * 4 + 1] = (byte) (gmul(0x09, s0) ^ gmul(0x0e, s1) ^ gmul(0x0b, s2) ^ gmul(0x0d, s3));
            state[c * 4 + 2] = (byte) (gmul(0x0d, s0) ^ gmul(0x09, s1) ^ gmul(0x0e, s2) ^ gmul(0x0b, s3));
            state[c * 4 + 3] = (byte) (gmul(0x0b, s0) ^ gmul(0x0d, s1) ^ gmul(0x09, s2) ^ gmul(0x0e, s3));
        }
    }
    
    // AddRoundKey
    private static void addRoundKey(byte[] state, byte[] roundKey) {
        for (int i = 0; i < 16; i++) {
            state[i] ^= roundKey[i];
        }
    }
    
    // AES加密单个块
    private static void encryptBlock(byte[] block, KeySchedule keySchedule) {
        addRoundKey(block, keySchedule.getRoundKey(0));
        
        for (int round = 1; round < ROUNDS; round++) {
            subBytes(block);
            shiftRows(block);
            mixColumns(block);
            addRoundKey(block, keySchedule.getRoundKey(round));
        }
        
        subBytes(block);
        shiftRows(block);
        addRoundKey(block, keySchedule.getRoundKey(ROUNDS));
    }
    
    // AES解密单个块
    private static void decryptBlock(byte[] block, KeySchedule keySchedule) {
        addRoundKey(block, keySchedule.getRoundKey(ROUNDS));
        
        for (int round = ROUNDS - 1; round > 0; round--) {
            invShiftRows(block);
            invSubBytes(block);
            addRoundKey(block, keySchedule.getRoundKey(round));
            invMixColumns(block);
        }
        
        invShiftRows(block);
        invSubBytes(block);
        addRoundKey(block, keySchedule.getRoundKey(0));
    }
    
    // PKCS5填充
    private static byte[] pkcs5Pad(byte[] data) {
        int padLen = BLOCK_SIZE - (data.length % BLOCK_SIZE);
        byte[] padded = new byte[data.length + padLen];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLen;
        }
        return padded;
    }
    
    // PKCS5去填充
    private static byte[] pkcs5Unpad(byte[] data) {
        int padLen = data[data.length - 1] & 0xff;
        if (padLen > BLOCK_SIZE || padLen <= 0) {
            throw new IllegalArgumentException("无效的填充");
        }
        byte[] unpadded = new byte[data.length - padLen];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }
    
    // 生成IV
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        long seed = System.nanoTime();
        for (int i = 0; i < IV_LENGTH; i++) {
            seed = (seed * 1103515245 + 12345) & 0x7fffffff;
            iv[i] = (byte) (seed & 0xff);
        }
        return iv;
    }
    
    public static void encryptFile(InputStream inputStream, OutputStream outputStream, String keyword) 
            throws IOException{
        // 使用程序生成确定性密钥
        byte[] key = generateKey(keyword);
        
        // 生成IV
        byte[] iv = generateIV();
        outputStream.write(iv);
        
        KeySchedule keySchedule = new KeySchedule(key);
        byte[] prevCipher = iv.clone();
        
        // 读取所有数据
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int len;
        while ((len = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, len);
        }
        
        byte[] data = buffer.toByteArray();
        byte[] padded = pkcs5Pad(data);
        
        // CBC模式加密
        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, i, block, 0, BLOCK_SIZE);
            
            // XOR with previous ciphertext
            for (int j = 0; j < BLOCK_SIZE; j++) {
                block[j] ^= prevCipher[j];
            }
            
            encryptBlock(block, keySchedule);
            outputStream.write(block);
            System.arraycopy(block, 0, prevCipher, 0, BLOCK_SIZE);
        }
    }
    
    public static void decryptFile(InputStream inputStream, OutputStream outputStream, String keyword, String fileName) 
            throws IOException{
        // 使用程序生成确定性密钥（与加密时相同）
        byte[] key = generateKey(keyword);
        
        // 读取IV
        byte[] iv = new byte[IV_LENGTH];
        int bytesRead = inputStream.read(iv);
        if (bytesRead != IV_LENGTH) {
            throw new IOException("无效的加密数据格式：无法读取IV（需要16字节）");
        }
        
        KeySchedule keySchedule = new KeySchedule(key);
        byte[] prevCipher = iv.clone();
        
        // 读取所有数据
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int len;
        while ((len = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, len);
        }
        
        byte[] encrypted = buffer.toByteArray();
        if (encrypted.length % BLOCK_SIZE != 0) {
            throw new IOException("无效的加密数据格式：数据长度不是16的倍数");
        }
        
        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
        
        // CBC模式解密
        for (int i = 0; i < encrypted.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(encrypted, i, block, 0, BLOCK_SIZE);
            byte[] cipherBlock = block.clone();
            
            decryptBlock(block, keySchedule);
            
            // XOR with previous ciphertext
            for (int j = 0; j < BLOCK_SIZE; j++) {
                block[j] ^= prevCipher[j];
            }
            
            decrypted.write(block);
            System.arraycopy(cipherBlock, 0, prevCipher, 0, BLOCK_SIZE);
        }
        
        // 去填充
        byte[] unpadded = pkcs5Unpad(decrypted.toByteArray());
        outputStream.write(unpadded);
        outputStream.flush();
    }
}
