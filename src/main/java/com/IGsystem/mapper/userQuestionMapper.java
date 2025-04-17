package com.IGsystem.mapper;
import com.IGsystem.entity.userQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface userQuestionMapper extends BaseMapper<userQuestion> {
    List<userQuestion> searchByKeyword(String keyword);
}
