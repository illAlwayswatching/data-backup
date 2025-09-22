package org.example.databackupback.common;

import lombok.Data;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 11:26
 **/
@Data
public class Response {
    private String message;

    private String type;

    private Object data;

    // 项目地址
    public static String PROJECT_PATH = System.getProperty("user.dir");

    public static String USER_DATA = PROJECT_PATH + "/data";

    public static Response success(String message) {
        Response r = new Response();
        r.setMessage(message);
        r.setType("success");
        r.setData(null);
        return r;
    }

    public static Response success(String message, Object data) {
        Response r = success(message);
        r.setData(data);
        return r;
    }

    public static Response error(String message) {
        Response r = new Response();
        r.setMessage(message);
        r.setType("error");
        r.setData(null);
        return r;
    }
}
