package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    List<Comment> selectCommentsByUserId(int userId, int entityType, int offset, int limit);

    int selectRepliedCommentCountByUserId(int userId, int entityType);

    Comment selectCommentById(int id);

}
