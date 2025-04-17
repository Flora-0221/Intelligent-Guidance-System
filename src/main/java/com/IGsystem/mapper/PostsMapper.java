package com.IGsystem.mapper;
import com.IGsystem.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface PostsMapper extends BaseMapper<Post> {
    List<Post> searchByKeyword(String keyword);
}
