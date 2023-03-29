package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    // 功能：我发布的帖子
    // 首页查询值不会传入userId，也就是传入0，我们不去管它，需要动态sql
    // 分页功能: offset: 每页的行号；limit: 每页最多有几条数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 因为需要分页，需要知道一共多少条数据，之后在与limit相除即可，所以也需要一个方法
    // @Param注解用于给参数取别名
    // 如果只有一个参数，并且在<if>里使用，则必须加别名，否则会报错
    int selectDiscussPostRows(@Param("userId") int userId);

    // 插入帖子
    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

}
