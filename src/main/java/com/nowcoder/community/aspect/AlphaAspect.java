package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))") // service包下的所有业务组件的所有方法的所有参数 返回的所有类型值
    public void pointcut() {

    }

    @Before("pointcut()") // 在切点之前
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()") // 在切点之后
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()") // 在返回之后
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()") // 在抛异常之后
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()") // 前后都织入逻辑
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}

