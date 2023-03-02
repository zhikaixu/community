package com.nowcoder.community.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("alphaHibernate") // 如果不加，spring容器不会扫描到这个bean，因为是数据访问，所以用repository
// @Primary // 如果有其他同类的impl，例如AlphaDaoMyBatisImpl,spring容器会无法选择扫描哪一个bean，因此使用primary加以区分
// 如果要同时让spring容器扫描同类接口的不同bean，可以在Repository注解后定义bean名
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }

}
