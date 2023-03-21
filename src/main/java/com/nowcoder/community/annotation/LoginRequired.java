package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 声明这个注解是注解于方法
@Retention(RetentionPolicy.RUNTIME) // 声明注解在运行时有效
public @interface LoginRequired {


}
