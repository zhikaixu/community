package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest; // spring 3.0
import jakarta.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpServletRequest; // spring 2.0用的是这个包
//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: " + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

//    @ExceptionHandler({Exception.class})
//    public String handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
//        logger.error("服务器发生异常: " + e.getMessage());
//        for (StackTraceElement element : e.getStackTrace()) {
//            logger.error(element.toString());
//        }
//        return CommunityUtil.getJSONString(1, "服务器异常!");
//        String xRequestedWith = request.getHeader("x-requested-with");
//        if ("XMLHttpRequest".equals(xRequestedWith)) {
//            response.setContentType("application/plain;charset=utf-8");
//            PrintWriter writer = response.getWriter();
//            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
//        } else {
//            response.sendRedirect(request.getContextPath() + "/error");
//        }
//}


}
