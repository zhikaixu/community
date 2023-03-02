package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
//如果想要变为多个实例，即每次访问bean，都会创建一个新的实例，可以在类上加上注解：
//@Scope(“prototype”)
//默认是@Scope("Single")
public class AlphaService {
    @Autowired
    // service依赖于dao
    private AlphaDao alphaDao;

    public AlphaService() {
        System.out.println("Instantiate AlphaService");
    }

    @PostConstruct // 在构造器运行之后调用
    public void init() {
        System.out.println("Initialize AlphaService");
    }

    @PreDestroy // 在销毁之前调用以便提前释放资源
    public void destroy() {
        System.out.println("Destroy AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }
}
