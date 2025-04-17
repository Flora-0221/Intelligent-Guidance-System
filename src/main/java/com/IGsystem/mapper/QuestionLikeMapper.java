package com.IGsystem.mapper;

import com.IGsystem.dto.PostDTO;
import com.IGsystem.entity.LikesQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface QuestionLikeMapper extends BaseMapper<LikesQuestion> {
}
