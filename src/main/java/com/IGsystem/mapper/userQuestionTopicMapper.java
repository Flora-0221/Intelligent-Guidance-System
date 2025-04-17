package com.IGsystem.mapper;
import com.IGsystem.entity.userQuestionTopic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface userQuestionTopicMapper extends BaseMapper<userQuestionTopic> {
    @Select("SELECT t.name FROM Topics t JOIN user_question_topic pt ON t.id = pt.topic_id WHERE pt.question_id = #{questionId}")
    List<String> selectTopicNamesByQuestionId(@Param("questionId")Long questionId);
}
