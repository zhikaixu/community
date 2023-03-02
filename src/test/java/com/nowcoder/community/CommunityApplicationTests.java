package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 测试时启用某个配置类
class CommunityApplicationTests implements ApplicationContextAware { // 让这个类得到Spring容器

	private ApplicationContext applicationContext; // 暂存Spring容器
	@Override
	// 这里会传入Spring容器ApplicationContext, 可以将容器暂存下来以便后续使用
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);
		// 从spring容器中获得已被扫描的bean
//		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		AlphaDao alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManagement() {
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	// 测试调用第三方bean配置文件
	public void testBeanConfig() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
	// 注入依赖：主动获取bean，而不是手动进行getBean操作
	@Qualifier("alphaHibernate") // 注入同类中的某个bean
	private AlphaDao alphaDao;

	@Test
	public void testDI() {
		System.out.println(alphaDao);
	}
}
