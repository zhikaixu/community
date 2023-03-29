package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // 只能加密不能解密
    // 简单的密码加密后也很简单，需要会使用salt这个随机字符串（包含在md5的包里了）
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    // 将服务器返回给浏览器的Json数据整合起来：
    // 1. 编码（必须有）
    // 2. 提示信息
    // 3. 业务数据
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toString();
    }

    // 可能没有业务数据
    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    // 可能没有提示信息和业务数据
    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    // 利用main方法来测试
//    public static void main(String[] args) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", "zhangsan");
//        map.put("age", 25);
//        System.out.println(getJSONString(0, "ok", map));
//    }

}
