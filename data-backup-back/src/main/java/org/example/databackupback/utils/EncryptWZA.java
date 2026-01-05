package org.example.databackupback.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class EncryptWZA {
    // 基础常量
    private static final int BLOCK_SIZE = 16;        // 块大小：16字节（128位）
    private static final int KEY_LENGTH = 16;        // 密钥长度：128位（简化版）
    private static final int IV_LENGTH = 16;         // IV长度：16字节
    private static final int ROUNDS = 12;            // 轮数：12轮（简化版，从32轮减到12轮）
    
    // Serpent算法：程序生成的S-box（4位输入 → 4位输出，16个值）
    private static final int[] S_BOX = generateSBox();
    private static final int[] INV_S_BOX = generateInvSBox();
    
    // Camellia算法：程序生成的S-box（8位输入 → 8位输出，256个值）
    private static final int[] CAM_S_BOX1 = generateCamSBox(1);
    private static final int[] CAM_S_BOX2 = generateCamSBox(2);
    private static final int[] CAM_S_BOX3 = generateCamSBox(3);
    private static final int[] CAM_S_BOX4 = generateCamSBox(4);
    private static final int[] CAM_INV_S_BOX1 = generateInvCamSBox(CAM_S_BOX1);
    private static final int[] CAM_INV_S_BOX2 = generateInvCamSBox(CAM_S_BOX2);
    private static final int[] CAM_INV_S_BOX3 = generateInvCamSBox(CAM_S_BOX3);
    private static final int[] CAM_INV_S_BOX4 = generateInvCamSBox(CAM_S_BOX4);
    
    // Camellia算法常量
    private static final int CAM_ROUNDS = 8; // 简化版：8轮（原18轮）
    
    // 固定常数（用于密钥扩展）
    private static final int PHI = 0x9e3779b9; // 黄金比例常数
    
    /**
     * 生成S-box（使用简单算法生成16个值）
     */
    private static int[] generateSBox() {
        int[] sbox = new int[16];
        // 使用简单置换生成S-box
        for (int i = 0; i < 16; i++) {
            int value = i;
            // 简单的非线性变换
            value = ((value << 1) | (value >>> 3)) & 0xf;
            value ^= 0xf;
            value = ((value << 2) ^ value) & 0xf;
            sbox[i] = value;
        }
        return sbox;
    }
    
    /**
     * 生成逆S-box
     */
    private static int[] generateInvSBox() {
        int[] invSbox = new int[16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (S_BOX[j] == i) {
                    invSbox[i] = j;
                    break;
                }
            }
        }
        return invSbox;
    }
    
    /**
     * 生成Camellia S-box（8位输入 → 8位输出）
     */
    private static int[] generateCamSBox(int seed) {
        int[] sbox = new int[256];
        // 使用不同的种子生成不同的S-box
        for (int i = 0; i < 256; i++) {
            int value = i;
            // 非线性变换（简化版）
            value = ((value << 1) | (value >>> 7)) & 0xff;
            value ^= (seed * 0x11) & 0xff;
            value = ((value << 2) ^ value) & 0xff;
            value ^= (seed * 0x37) & 0xff;
            value = ((value << 3) ^ value) & 0xff;
            // 确保每个值唯一
            value = (value + seed * i) & 0xff;
            sbox[i] = value;
        }
        return sbox;
    }
    
    /**
     * 生成Camellia逆S-box
     */
    private static int[] generateInvCamSBox(int[] sbox) {
        int[] invSbox = new int[256];
        for (int i = 0; i < 256; i++) {
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
     * 从keyword生成确定性密钥（类似AES）
     */
    private static byte[] generateKey(String keyword) {
        byte[] keyBytes = keyword.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[KEY_LENGTH];
        
        long hash = 0;
        for (byte b : keyBytes) {
            hash = hash * 31 + (b & 0xff);
        }
        
        for (int i = 0; i < KEY_LENGTH; i++) {
            if (i < keyBytes.length) {
                key[i] = keyBytes[i];
            } else {
                hash = hash * 1103515245 + 12345;
                key[i] = (byte) (hash & 0xff);
            }
        }
        
        // 进一步混合
        for (int round = 0; round < 4; round++) {
            for (int i = 0; i < KEY_LENGTH; i++) {
                int j = (i + 1) % KEY_LENGTH;
                key[i] = (byte) ((key[i] ^ key[j] ^ (hash >> (i * 8))) & 0xff);
            }
        }
        
        return key;
    }
    
    /**
     * 简化的线性变换（对4个32位字进行位操作）
     */
    private static void linearTransform(int[] words) {
        // 简化的线性变换：循环左移 + XOR
        words[0] = Integer.rotateLeft(words[0], 13);
        words[2] = Integer.rotateLeft(words[2], 3);
        words[1] ^= words[0] ^ words[2];
        words[3] ^= words[2] ^ (words[0] << 3);
        words[1] = Integer.rotateLeft(words[1], 1);
        words[3] = Integer.rotateLeft(words[3], 7);
        words[0] ^= words[1] ^ words[3];
        words[2] ^= words[3] ^ (words[1] << 7);
        words[0] = Integer.rotateLeft(words[0], 5);
        words[2] = Integer.rotateLeft(words[2], 22);
    }
    
    /**
     * 逆线性变换
     */
    private static void invLinearTransform(int[] words) {
        // 反向操作
        words[2] = Integer.rotateRight(words[2], 22);
        words[0] = Integer.rotateRight(words[0], 5);
        words[2] ^= words[3] ^ (words[1] << 7);
        words[0] ^= words[1] ^ words[3];
        words[3] = Integer.rotateRight(words[3], 7);
        words[1] = Integer.rotateRight(words[1], 1);
        words[3] ^= words[2] ^ (words[0] << 3);
        words[1] ^= words[0] ^ words[2];
        words[2] = Integer.rotateRight(words[2], 3);
        words[0] = Integer.rotateRight(words[0], 13);
    }
    
    /**
     * 密钥扩展：将128位密钥扩展为12个128位子密钥
     */
    private static byte[][] expandKey(byte[] key) {
        byte[][] roundKeys = new byte[ROUNDS + 1][16];
        
        // 初始密钥
        System.arraycopy(key, 0, roundKeys[0], 0, 16);
        
        // 扩展后续轮密钥
        for (int round = 1; round <= ROUNDS; round++) {
            // 将上一轮密钥转换为4个32位字
            int[] words = new int[4];
            for (int i = 0; i < 4; i++) {
                words[i] = ((roundKeys[round - 1][i * 4] & 0xff) << 24) |
                           ((roundKeys[round - 1][i * 4 + 1] & 0xff) << 16) |
                           ((roundKeys[round - 1][i * 4 + 2] & 0xff) << 8) |
                           (roundKeys[round - 1][i * 4 + 3] & 0xff);
            }
            
            // 应用S-box到每个字节的低4位
            for (int i = 0; i < 4; i++) {
                int w = words[i];
                int result = 0;
                for (int j = 0; j < 4; j++) {
                    int nibble = (w >>> (j * 8)) & 0xf;
                    int subNibble = S_BOX[nibble];
                    result |= (subNibble << (j * 8));
                }
                words[i] = result;
            }
            
            // 添加轮常数
            words[0] ^= (PHI * round) & 0xffffffff;
            
            // 简化的密钥混合
            words[0] ^= words[1];
            words[2] ^= words[3];
            words[1] = Integer.rotateLeft(words[1], 3);
            words[3] = Integer.rotateLeft(words[3], 7);
            words[1] ^= words[0];
            words[3] ^= words[2];
            
            // 转换回字节数组
            for (int i = 0; i < 4; i++) {
                roundKeys[round][i * 4] = (byte) (words[i] >>> 24);
                roundKeys[round][i * 4 + 1] = (byte) (words[i] >>> 16);
                roundKeys[round][i * 4 + 2] = (byte) (words[i] >>> 8);
                roundKeys[round][i * 4 + 3] = (byte) words[i];
            }
        }
        
        return roundKeys;
    }
    
    /**
     * S-box查找（应用到块的每个4位）
     */
    private static void applySBox(byte[] block, int[] sbox) {
        for (int i = 0; i < block.length; i++) {
            int lowNibble = block[i] & 0xf;
            int highNibble = (block[i] >>> 4) & 0xf;
            block[i] = (byte) ((sbox[highNibble] << 4) | sbox[lowNibble]);
        }
    }
    
    /**
     * 单块加密
     */
    private static void encryptBlock(byte[] block, byte[][] roundKeys) {
        // 将块转换为4个32位字
        int[] words = new int[4];
        for (int i = 0; i < 4; i++) {
            words[i] = ((block[i * 4] & 0xff) << 24) |
                       ((block[i * 4 + 1] & 0xff) << 16) |
                       ((block[i * 4 + 2] & 0xff) << 8) |
                       (block[i * 4 + 3] & 0xff);
        }
        
        // 初始轮密钥加
        int[] keyWords = new int[4];
        for (int i = 0; i < 4; i++) {
            keyWords[i] = ((roundKeys[0][i * 4] & 0xff) << 24) |
                          ((roundKeys[0][i * 4 + 1] & 0xff) << 16) |
                          ((roundKeys[0][i * 4 + 2] & 0xff) << 8) |
                          (roundKeys[0][i * 4 + 3] & 0xff);
            words[i] ^= keyWords[i];
        }
        
        // 12轮加密
        for (int round = 1; round <= ROUNDS; round++) {
            // S-box替换（应用到字节）
            applySBox(bytesFromWords(words), S_BOX);
            words = wordsFromBytes(bytesFromWords(words));
            
            // 线性变换
            linearTransform(words);
            
            // 轮密钥加
            for (int i = 0; i < 4; i++) {
                keyWords[i] = ((roundKeys[round][i * 4] & 0xff) << 24) |
                              ((roundKeys[round][i * 4 + 1] & 0xff) << 16) |
                              ((roundKeys[round][i * 4 + 2] & 0xff) << 8) |
                              (roundKeys[round][i * 4 + 3] & 0xff);
                words[i] ^= keyWords[i];
            }
        }
        
        // 转换回字节
        byte[] result = bytesFromWords(words);
        System.arraycopy(result, 0, block, 0, 16);
    }
    
    /**
     * 单块解密
     */
    private static void decryptBlock(byte[] block, byte[][] roundKeys) {
        // 将块转换为4个32位字
        int[] words = new int[4];
        for (int i = 0; i < 4; i++) {
            words[i] = ((block[i * 4] & 0xff) << 24) |
                       ((block[i * 4 + 1] & 0xff) << 16) |
                       ((block[i * 4 + 2] & 0xff) << 8) |
                       (block[i * 4 + 3] & 0xff);
        }
        
        // 反向12轮解密
        for (int round = ROUNDS; round >= 1; round--) {
            // 轮密钥加
            int[] keyWords = new int[4];
            for (int i = 0; i < 4; i++) {
                keyWords[i] = ((roundKeys[round][i * 4] & 0xff) << 24) |
                              ((roundKeys[round][i * 4 + 1] & 0xff) << 16) |
                              ((roundKeys[round][i * 4 + 2] & 0xff) << 8) |
                              (roundKeys[round][i * 4 + 3] & 0xff);
                words[i] ^= keyWords[i];
            }
            
            // 逆线性变换
            invLinearTransform(words);
            
            // 逆S-box替换
            applySBox(bytesFromWords(words), INV_S_BOX);
            words = wordsFromBytes(bytesFromWords(words));
        }
        
        // 初始轮密钥加
        int[] keyWords = new int[4];
        for (int i = 0; i < 4; i++) {
            keyWords[i] = ((roundKeys[0][i * 4] & 0xff) << 24) |
                          ((roundKeys[0][i * 4 + 1] & 0xff) << 16) |
                          ((roundKeys[0][i * 4 + 2] & 0xff) << 8) |
                          (roundKeys[0][i * 4 + 3] & 0xff);
            words[i] ^= keyWords[i];
        }
        
        // 转换回字节
        byte[] result = bytesFromWords(words);
        System.arraycopy(result, 0, block, 0, 16);
    }
    
    /**
     * 辅助方法：从words转换为bytes
     */
    private static byte[] bytesFromWords(int[] words) {
        byte[] bytes = new byte[16];
        for (int i = 0; i < 4; i++) {
            bytes[i * 4] = (byte) (words[i] >>> 24);
            bytes[i * 4 + 1] = (byte) (words[i] >>> 16);
            bytes[i * 4 + 2] = (byte) (words[i] >>> 8);
            bytes[i * 4 + 3] = (byte) words[i];
        }
        return bytes;
    }
    
    /**
     * 辅助方法：从bytes转换为words
     */
    private static int[] wordsFromBytes(byte[] bytes) {
        int[] words = new int[4];
        for (int i = 0; i < 4; i++) {
            words[i] = ((bytes[i * 4] & 0xff) << 24) |
                       ((bytes[i * 4 + 1] & 0xff) << 16) |
                       ((bytes[i * 4 + 2] & 0xff) << 8) |
                       (bytes[i * 4 + 3] & 0xff);
        }
        return words;
    }
    
    /**
     * PKCS5填充
     */
    private static byte[] pkcs5Pad(byte[] data) {
        int padLen = BLOCK_SIZE - (data.length % BLOCK_SIZE);
        byte[] padded = new byte[data.length + padLen];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLen;
        }
        return padded;
    }
    
    /**
     * PKCS5去填充
     */
    private static byte[] pkcs5Unpad(byte[] data) {
        int padLen = data[data.length - 1] & 0xff;
        if (padLen > BLOCK_SIZE || padLen <= 0) {
            throw new IllegalArgumentException("无效的填充");
        }
        byte[] unpadded = new byte[data.length - padLen];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }
    
    /**
     * 生成IV
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        long seed = System.nanoTime();
        for (int i = 0; i < IV_LENGTH; i++) {
            seed = (seed * 1103515245 + 12345) & 0x7fffffff;
            iv[i] = (byte) (seed & 0xff);
        }
        return iv;
    }
    
    // ==================== Camellia算法实现 ====================
    
    /**
     * Camellia F函数（简化版）
     */
    private static long camF(long fInput, long ke) {
        long x = fInput ^ ke;
        
        // S-box替换（使用4个S-box）
        long t1 = camSBox1((byte) (x >>> 56));
        long t2 = camSBox2((byte) ((x >>> 48) & 0xff));
        long t3 = camSBox3((byte) ((x >>> 40) & 0xff));
        long t4 = camSBox4((byte) ((x >>> 32) & 0xff));
        long t5 = camSBox2((byte) ((x >>> 24) & 0xff));
        long t6 = camSBox3((byte) ((x >>> 16) & 0xff));
        long t7 = camSBox4((byte) ((x >>> 8) & 0xff));
        long t8 = camSBox1((byte) (x & 0xff));
        
        long y1 = t1 ^ t3 ^ t4 ^ t6 ^ t7 ^ t8;
        long y2 = t1 ^ t2 ^ t4 ^ t5 ^ t7 ^ t8;
        long y3 = t1 ^ t2 ^ t3 ^ t5 ^ t6 ^ t8;
        long y4 = t2 ^ t3 ^ t4 ^ t5 ^ t6 ^ t7;
        long y5 = t1 ^ t2 ^ t6 ^ t7 ^ t8;
        long y6 = t2 ^ t3 ^ t5 ^ t7 ^ t8;
        long y7 = t3 ^ t4 ^ t5 ^ t6 ^ t8;
        long y8 = t1 ^ t4 ^ t5 ^ t6 ^ t7;
        
        return ((y1 & 0xffL) << 56) | ((y2 & 0xffL) << 48) | ((y3 & 0xffL) << 40) | ((y4 & 0xffL) << 32) |
               ((y5 & 0xffL) << 24) | ((y6 & 0xffL) << 16) | ((y7 & 0xffL) << 8) | (y8 & 0xffL);
    }
    
    /**
     * Camellia 逆F函数（使用逆S-box）
     */
    private static long camFInv(long fInput, long ke) {
        long x = fInput ^ ke;
        
        // 逆S-box替换（使用4个逆S-box）
        long t1 = CAM_INV_S_BOX1[(int) ((x >>> 56) & 0xff)] & 0xffL;
        long t2 = CAM_INV_S_BOX2[(int) ((x >>> 48) & 0xff)] & 0xffL;
        long t3 = CAM_INV_S_BOX3[(int) ((x >>> 40) & 0xff)] & 0xffL;
        long t4 = CAM_INV_S_BOX4[(int) ((x >>> 32) & 0xff)] & 0xffL;
        long t5 = CAM_INV_S_BOX2[(int) ((x >>> 24) & 0xff)] & 0xffL;
        long t6 = CAM_INV_S_BOX3[(int) ((x >>> 16) & 0xff)] & 0xffL;
        long t7 = CAM_INV_S_BOX4[(int) ((x >>> 8) & 0xff)] & 0xffL;
        long t8 = CAM_INV_S_BOX1[(int) (x & 0xff)] & 0xffL;
        
        long y1 = t1 ^ t3 ^ t4 ^ t6 ^ t7 ^ t8;
        long y2 = t1 ^ t2 ^ t4 ^ t5 ^ t7 ^ t8;
        long y3 = t1 ^ t2 ^ t3 ^ t5 ^ t6 ^ t8;
        long y4 = t2 ^ t3 ^ t4 ^ t5 ^ t6 ^ t7;
        long y5 = t1 ^ t2 ^ t6 ^ t7 ^ t8;
        long y6 = t2 ^ t3 ^ t5 ^ t7 ^ t8;
        long y7 = t3 ^ t4 ^ t5 ^ t6 ^ t8;
        long y8 = t1 ^ t4 ^ t5 ^ t6 ^ t7;
        
        return ((y1 & 0xffL) << 56) | ((y2 & 0xffL) << 48) | ((y3 & 0xffL) << 40) | ((y4 & 0xffL) << 32) |
               ((y5 & 0xffL) << 24) | ((y6 & 0xffL) << 16) | ((y7 & 0xffL) << 8) | (y8 & 0xffL);
    }
    
    /**
     * Camellia S-box查找辅助方法
     */
    private static int camSBox1(int index) {
        return CAM_S_BOX1[index & 0xff] & 0xff;
    }
    
    private static int camSBox2(int index) {
        return CAM_S_BOX2[index & 0xff] & 0xff;
    }
    
    private static int camSBox3(int index) {
        return CAM_S_BOX3[index & 0xff] & 0xff;
    }
    
    private static int camSBox4(int index) {
        return CAM_S_BOX4[index & 0xff] & 0xff;
    }
    
    /**
     * Camellia FL函数（简化版）
     */
    private static long camFL(long flInput, long ke) {
        long x1 = (flInput >>> 32) & 0xffffffffL;
        long x2 = flInput & 0xffffffffL;
        long k1 = (ke >>> 32) & 0xffffffffL;
        long k2 = ke & 0xffffffffL;
        
        x2 ^= Integer.rotateLeft((int) ((x1 & k1) & 0xffffffffL), 1);
        x1 ^= (x2 | k2) & 0xffffffffL;
        
        return (x1 << 32) | (x2 & 0xffffffffL);
    }
    
    /**
     * Camellia FLINV函数（FL的逆）
     */
    private static long camFLINV(long flinvInput, long ke) {
        long y1 = (flinvInput >>> 32) & 0xffffffffL;
        long y2 = flinvInput & 0xffffffffL;
        long k1 = (ke >>> 32) & 0xffffffffL;
        long k2 = ke & 0xffffffffL;
        
        y1 ^= (y2 | k2) & 0xffffffffL;
        y2 ^= Integer.rotateLeft((int) ((y1 & k1) & 0xffffffffL), 1);
        
        return (y1 << 32) | (y2 & 0xffffffffL);
    }
    
    /**
     * Camellia密钥扩展
     */
    private static long[] expandCamKey(byte[] key) {
        long[] roundKeys = new long[CAM_ROUNDS * 2 + 2]; // 每个轮需要2个密钥，加上FL密钥
        
        // 将密钥转换为两个64位字
        long kl = ((key[0] & 0xffL) << 56) | ((key[1] & 0xffL) << 48) | 
                  ((key[2] & 0xffL) << 40) | ((key[3] & 0xffL) << 32) |
                  ((key[4] & 0xffL) << 24) | ((key[5] & 0xffL) << 16) |
                  ((key[6] & 0xffL) << 8) | (key[7] & 0xffL);
        long kr = ((key[8] & 0xffL) << 56) | ((key[9] & 0xffL) << 48) |
                  ((key[10] & 0xffL) << 40) | ((key[11] & 0xffL) << 32) |
                  ((key[12] & 0xffL) << 24) | ((key[13] & 0xffL) << 16) |
                  ((key[14] & 0xffL) << 8) | (key[15] & 0xffL);
        
        // 生成轮密钥
        long ka = kl ^ kr;
        ka = camF(ka, 0);
        kr ^= ka;
        ka = camF(ka, kr);
        kl ^= ka;
        
        // 生成每轮的密钥
        for (int i = 0; i < CAM_ROUNDS; i++) {
            if (i % 2 == 0) {
                roundKeys[i * 2] = kl;
                roundKeys[i * 2 + 1] = kr;
                // 轮密钥更新（64位循环左移15位）
                kl = Long.rotateLeft(kl, 15);
            } else {
                roundKeys[i * 2] = kr;
                roundKeys[i * 2 + 1] = kl;
                // 轮密钥更新（64位循环左移15位）
                kr = Long.rotateLeft(kr, 15);
            }
        }
        
        // FL密钥（用于简化版）
        roundKeys[CAM_ROUNDS * 2] = ka;
        roundKeys[CAM_ROUNDS * 2 + 1] = kl ^ kr;
        
        return roundKeys;
    }
    
    /**
     * Camellia单块加密
     */
    private static void encryptCamBlock(byte[] block, long[] roundKeys) {
        // 将块转换为两个64位字
        long left = ((block[0] & 0xffL) << 56) | ((block[1] & 0xffL) << 48) |
                    ((block[2] & 0xffL) << 40) | ((block[3] & 0xffL) << 32) |
                    ((block[4] & 0xffL) << 24) | ((block[5] & 0xffL) << 16) |
                    ((block[6] & 0xffL) << 8) | (block[7] & 0xffL);
        long right = ((block[8] & 0xffL) << 56) | ((block[9] & 0xffL) << 48) |
                     ((block[10] & 0xffL) << 40) | ((block[11] & 0xffL) << 32) |
                     ((block[12] & 0xffL) << 24) | ((block[13] & 0xffL) << 16) |
                     ((block[14] & 0xffL) << 8) | (block[15] & 0xffL);
        
        // 8轮Feistel结构
        for (int i = 0; i < CAM_ROUNDS; i++) {
            long temp = right;
            right = left ^ camF(right, roundKeys[i * 2]);
            left = temp;
            
            // 在偶数轮后应用FL（简化版：每2轮一次）
            if ((i + 1) % 2 == 0 && i < CAM_ROUNDS - 1) {
                left = camFL(left, roundKeys[CAM_ROUNDS * 2]);
                right = camFLINV(right, roundKeys[CAM_ROUNDS * 2 + 1]);
            }
        }
        
        // 最后交换
        long temp = left;
        left = right;
        right = temp;
        
        // 转换回字节
        for (int i = 0; i < 8; i++) {
            block[i] = (byte) (left >>> (56 - i * 8));
            block[i + 8] = (byte) (right >>> (56 - i * 8));
        }
    }

    /**
     * Camellia单块解密
     */
    private static void decryptCamBlock(byte[] block, long[] roundKeys) {
        // 将块转换为两个64位字
        long left = ((block[0] & 0xffL) << 56) | ((block[1] & 0xffL) << 48) |
                    ((block[2] & 0xffL) << 40) | ((block[3] & 0xffL) << 32) |
                    ((block[4] & 0xffL) << 24) | ((block[5] & 0xffL) << 16) |
                    ((block[6] & 0xffL) << 8) | (block[7] & 0xffL);
        long right = ((block[8] & 0xffL) << 56) | ((block[9] & 0xffL) << 48) |
                     ((block[10] & 0xffL) << 40) | ((block[11] & 0xffL) << 32) |
                     ((block[12] & 0xffL) << 24) | ((block[13] & 0xffL) << 16) |
                     ((block[14] & 0xffL) << 8) | (block[15] & 0xffL);
        
        // 首先取消最后的交换（加密时最后交换了）
        long temp = left;
        left = right;
        right = temp;
        
        // 反向8轮Feistel结构
        // 注意：在加密时，FL在轮之后应用；在解密时，需要在轮之前取消FL
        for (int i = CAM_ROUNDS - 1; i >= 0; i--) {
            // 在Feistel轮之前取消FL（如果该轮在加密时应用了FL）
            // 在加密时，FL在i=1,3,5轮之后应用（即在Round 1,3,5之后）
            // 在解密时，需要在进入Round 2,4,6之前先取消FL
            // 所以条件是：i+1对应加密时的位置，需要(i+1+1)%2==0，即(i+2)%2==0，也就是i%2==0
            if ((i + 1) % 2 == 0 && i < CAM_ROUNDS - 1) {
                // 取消FL变换：加密时是FL(left)和FLINV(right)，解密时需要逆操作
                left = camFLINV(left, roundKeys[CAM_ROUNDS * 2]);
                right = camFL(right, roundKeys[CAM_ROUNDS * 2 + 1]);
            }
            
            // Feistel网络的逆运算：L = R' XOR F(L', K), R = L'
            temp = left;
            left = right ^ camF(left, roundKeys[i * 2]);
            right = temp;
        }
        
        // 转换回字节
        for (int i = 0; i < 8; i++) {
            block[i] = (byte) (left >>> (56 - i * 8));
            block[i + 8] = (byte) (right >>> (56 - i * 8));
        }
    }

    /**
     * Serpent加密（CBC模式）
     */
    public static void encryptSerpent(InputStream inputStream, OutputStream outputStream, String password) 
            throws IOException {
        byte[] key = generateKey(password);
        byte[] iv = generateIV();
        outputStream.write(iv);
        
        byte[][] roundKeys = expandKey(key);
        
        // 读取所有数据
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int len;
        while ((len = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, len);
        }
        
        byte[] data = buffer.toByteArray();
        byte[] padded = pkcs5Pad(data);
        
        byte[] prevCipher = iv.clone();
        
        // CBC模式加密
        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, i, block, 0, BLOCK_SIZE);
            
            // XOR with previous ciphertext
            for (int j = 0; j < BLOCK_SIZE; j++) {
                block[j] ^= prevCipher[j];
            }
            
            encryptBlock(block, roundKeys);
            outputStream.write(block);
            System.arraycopy(block, 0, prevCipher, 0, BLOCK_SIZE);
        }
    }
    
    /**
     * Serpent解密（CBC模式）
     */
    public static void decryptSerpent(InputStream inputStream, OutputStream outputStream, String password, String fileName) 
            throws IOException {
        byte[] key = generateKey(password);
        
        // 读取IV
        byte[] iv = new byte[IV_LENGTH];
        int bytesRead = inputStream.read(iv);
        if (bytesRead != IV_LENGTH) {
            throw new IOException("无效的加密数据格式：无法读取IV");
        }
        
        byte[][] roundKeys = expandKey(key);
        
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
        byte[] prevCipher = iv.clone();
        
        // CBC模式解密
        for (int i = 0; i < encrypted.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(encrypted, i, block, 0, BLOCK_SIZE);
            byte[] cipherBlock = block.clone();
            
            decryptBlock(block, roundKeys);
            
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
    
    /**
     * Camellia加密（CBC模式）
     */
    public static void encryptCamellia(InputStream inputStream, OutputStream outputStream, String password) 
            throws IOException {
        byte[] key = generateKey(password);
        byte[] iv = generateIV();
        outputStream.write(iv);
        
        long[] roundKeys = expandCamKey(key);
        
        // 读取所有数据
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int len;
        while ((len = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, len);
        }
        
        byte[] data = buffer.toByteArray();
        byte[] padded = pkcs5Pad(data);
        
        byte[] prevCipher = iv.clone();
        
        // CBC模式加密
        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, i, block, 0, BLOCK_SIZE);
            
            // XOR with previous ciphertext
            for (int j = 0; j < BLOCK_SIZE; j++) {
                block[j] ^= prevCipher[j];
            }
            
            encryptCamBlock(block, roundKeys);
            outputStream.write(block);
            System.arraycopy(block, 0, prevCipher, 0, BLOCK_SIZE);
        }
    }
    
    /**
     * Camellia解密（CBC模式）
     */
    public static void decryptCamellia(InputStream inputStream, OutputStream outputStream, String password, String fileName) 
            throws IOException {
        byte[] key = generateKey(password);
        
        // 读取IV
        byte[] iv = new byte[IV_LENGTH];
        int bytesRead = inputStream.read(iv);
        if (bytesRead != IV_LENGTH) {
            throw new IOException("无效的加密数据格式：无法读取IV");
        }
        
        long[] roundKeys = expandCamKey(key);
        
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
        byte[] prevCipher = iv.clone();
        
        // CBC模式解密
        for (int i = 0; i < encrypted.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(encrypted, i, block, 0, BLOCK_SIZE);
            byte[] cipherBlock = block.clone();
            
            decryptCamBlock(block, roundKeys);
            
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
    
    /**
     * 公共接口：加密（兼容原有接口）
     */
    public static void encrypt(String algorithm, InputStream inputStream, 
                              OutputStream outputStream, String password) throws Exception {
        if (SERPENT.equals(algorithm)) {
            encryptSerpent(inputStream, outputStream, password);
        } else if (CAMELLIA.equals(algorithm)) {
            encryptCamellia(inputStream, outputStream, password);
        } else {
            throw new IllegalArgumentException("不支持的算法: " + algorithm);
        }
    }
    
    /**
     * 公共接口：解密（兼容原有接口）
     */
    public static void decrypt(String algorithm, InputStream inputStream, 
                              OutputStream outputStream, String password, String fileName) throws Exception {
        if (SERPENT.equals(algorithm)) {
            decryptSerpent(inputStream, outputStream, password, fileName);
        } else if (CAMELLIA.equals(algorithm)) {
            decryptCamellia(inputStream, outputStream, password, fileName);
        } else {
            throw new IllegalArgumentException("不支持的算法: " + algorithm);
        }
    }
    
    // 算法常量
    public static final String SERPENT = "Serpent";
    public static final String CAMELLIA = "Camellia";
}


