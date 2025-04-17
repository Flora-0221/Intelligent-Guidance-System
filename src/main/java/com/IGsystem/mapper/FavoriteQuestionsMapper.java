package com.IGsystem.mapper;

import com.IGsystem.dto.FavoriteQuestions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FavoriteQuestionsMapper extends BaseMapper<FavoriteQuestions> {

}
