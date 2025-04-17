package com.IGsystem.mapper;

import com.IGsystem.dto.SAQuestion;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SAQuestionMapper extends BaseMapper<SAQuestion> {
}
