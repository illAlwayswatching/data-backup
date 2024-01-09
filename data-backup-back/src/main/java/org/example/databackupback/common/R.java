package org.example.databackupback.common;

import lombok.Data;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 16:32
 **/
@Data
public class R {
    private String message;

    private String type;

    private Object data;

    // 项目地址
    public static String PROJECT_PATH = System.getProperty("user.dir");

    public static String USER_DATA = PROJECT_PATH + "/data";

    public static R success(String message) {
        R r = new R();
        r.setMessage(message);
        r.setType("success");
        r.setData(null);
        return r;
    }

    public static R success(String message, Object data) {
        R r = success(message);
        r.setData(data);
        return r;
    }

    public static R error(String message) {
        R r = new R();
        r.setMessage(message);
        r.setType("error");
        r.setData(null);
        return r;
    }
}
