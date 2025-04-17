package com.IGsystem.mapper;

import com.IGsystem.entity.TestResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TestResultMapper extends BaseMapper<TestResult> {
    //根据用户id查询得分列表
    List<Double> getUserScoreListByUserId(@Param("userId") Long userId);
}
