package com.IGsystem.mapper;

import com.IGsystem.entity.Topics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TopicMapper extends BaseMapper<Topics> {
    Topics selectByName(@Param("name") String name);
}
