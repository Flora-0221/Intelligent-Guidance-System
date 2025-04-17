package com.IGsystem.mapper;

import com.IGsystem.dto.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Repository
@Mapper
public interface QuestionMapper extends BaseMapper<Question>{
    List<Question> selectByLabels(@Param("grade") String grade, @Param("subject") String subject,
                                  @Param("task") String task, @Param("category") String category,
                                  @Param("topic") String topic);
    List<Question> selectLabels(@Param("grade") String grade, @Param("subject") String subject,
                                  @Param("task") String task, @Param("category") String category,
                                  @Param("topic") String topic);


    @Select("SELECT DISTINCT skill FROM problems")
    List<Question> selectDistinctSkills();

}
