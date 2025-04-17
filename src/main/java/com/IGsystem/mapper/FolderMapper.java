package com.IGsystem.mapper;

import com.IGsystem.dto.Folder;
import com.IGsystem.dto.SAQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FolderMapper extends BaseMapper<Folder> {

}
