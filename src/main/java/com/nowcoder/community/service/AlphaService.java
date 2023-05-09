package com.nowcoder.community.service;

import com.nowcoder.community.controller.UserController;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
//如果想要变为多个实例，即每次访问bean，都会创建一个新的实例，可以在类上加上注解：
//@Scope(“prototype”)
//默认是@Scope("Single")
public class AlphaService {
    @Autowired
    // service依赖于dao
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

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

    // 声明式事务
    // 最常用的三种传播机制
    // 业务方法A可能会调用业务方法B，这两者都可能有事务管理的标注，在调用B时，B的事务以谁的机制为准
    // REQUIRED：支持当前事务（A调用B，A就是当前事务/外部事务），如果外部事务不存在则创建新事务
    // REQUIRES_NEW：创建一个新事务，并且暂停当前事务（外部事务）
    // NESTED：如果当前存在事务（外部事务），则嵌套在该事务中执行（B嵌套在A的事务中，有独立的提交和回滚），如果外部事务不存在，就和REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

//        Integer.valueOf("abc"); // 这里会报错，需要回滚

        return "ok";
    }

    // 编程式事务
    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

//                Integer.valueOf("abc"); // 这里会报错，需要回滚

                return "ok";
            }
        });
    }

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    // 让该方法在多线程环境下被异步调用
    @Async
    public void execute1() {
        logger.debug("execute1");
    }

    @Scheduled(initialDelay = 10000, fixedRate = 1000) // 只要有程序在跑就会被调用
    public void execute2() {
        logger.debug("execute2");
    }

}
