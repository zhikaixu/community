package com.nowcoder.community.actuator;


import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    // 通过连接池来尝试获取连接与数据库
    @Autowired
    private DataSource dataSource;

    @ReadOperation // 表示通过get请求访问
    public String checkConnection() {
        try (
                Connection conn = dataSource.getConnection();
                ) {
            return CommunityUtil.getJSONString(0, "Connect Successfully!");
        } catch (SQLException e) {
            logger.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(1, "Fail to connect!");
        }
    }

}
