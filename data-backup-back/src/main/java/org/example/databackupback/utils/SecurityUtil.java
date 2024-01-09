package org.example.databackupback.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 20:53
 **/
public class SecurityUtil {
    /**
     * 生成BCryptPasswordEncoder密码
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 密码是否匹配
     */
    public static Boolean passwordMatch(String verified, String verifier) {
        return BCrypt.checkpw(verified, verifier);
    }
}
