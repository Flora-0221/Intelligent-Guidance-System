package com.IGsystem.mapper;
import com.IGsystem.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentsMapper extends BaseMapper<Comment> {
    @Select("SELECT * FROM comment WHERE post_id = #{postId}")
    List<Comment> getCommentsByPostId(@Param("postId") Long postId);
}
