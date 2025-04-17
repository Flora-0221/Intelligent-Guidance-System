package com.IGsystem.mapper;
import com.IGsystem.entity.PostTopic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PostTopicMapper extends BaseMapper<PostTopic> {
    @Select("SELECT t.name FROM Topics t JOIN Post_Topic pt ON t.id = pt.topic_id WHERE pt.post_id = #{postId}")
    List<String> selectTopicNamesByPostId(@Param("postId")Long postId);
}
