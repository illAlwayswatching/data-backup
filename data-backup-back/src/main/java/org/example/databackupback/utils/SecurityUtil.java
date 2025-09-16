package org.example.databackupback.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/12 23:53
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
