package com.IGsystem.mapper;

import com.IGsystem.entity.Comment;
import com.IGsystem.entity.commentQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentsQuestionMapper extends BaseMapper<commentQuestion> {
    @Select("SELECT * FROM comment_question WHERE question_id = #{questionId}")
    List<commentQuestion> getCommentsByQuestionId(@Param("questionId") Long questionId);
}
