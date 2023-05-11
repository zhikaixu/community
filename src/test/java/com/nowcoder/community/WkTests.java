package com.nowcoder.community;

import java.io.IOException;

public class WkTests {

    public static void main(String[] args) {
        String cmd = "wkhtmltoimage --quality 75 https://www.nowcoder.com /Users/zhikaixu/Desktop/nowcoder/data/wk-images/test3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
            Thread.sleep(3000); // bug
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
