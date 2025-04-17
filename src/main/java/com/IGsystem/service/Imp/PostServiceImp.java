package com.IGsystem.service.Imp;

import cn.hutool.core.lang.Console;
import com.IGsystem.dto.*;
import com.IGsystem.entity.*;
import com.IGsystem.mapper.*;
import com.IGsystem.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
@Service
@Slf4j
public class PostServiceImp extends ServiceImpl<PostsMapper, Post> implements PostService {

    @Autowired
    private PostsMapper postsMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private PostTopicMapper postTopicMapper;

    @Override
    public Result getAllPosts() {
        List<Post> posts = postsMapper.selectList(null);
        List<PostDTO> postDTOs = posts.stream().map(this::convertToPostDTO).collect(Collectors.toList());
        return Result.ok(postDTOs, (long) postDTOs.size());
    }

    @Override
    public Result getAllTopics() {
        List<Topics> topics = topicMapper.selectList(null);

        List<TopicsDTO> topicsDTOList = topics.stream()
                .map(TopicsDTO::new)
                .collect(Collectors.toList());

        return Result.ok(topicsDTOList, (long) topicsDTOList.size());
    }


    @Override
    public Result getPostById(Long id) {
        Post post = postsMapper.selectById(id);
        if (post == null) {
            return Result.fail("Post not found");
        }
        return Result.ok(convertToPostDTO(post));
    }

    @Override
    @Transactional
    public Result createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        User author = userMapper.selectById(postDTO.getAuthorId());
        if (author == null) {
            return Result.fail("User not found");
        }
        post.setAuthorId(author.getId());

        // 插入帖子到数据库
        postsMapper.insert(post);

        // 处理话题名称列表
        if (postDTO.getTopics() != null && !postDTO.getTopics().isEmpty()) {
            List<Topics> topics = postDTO.getTopics().stream()
                    .map(topicName -> {
                        Topics topic = topicMapper.selectByName(topicName);
                        if (topic == null) {
                            // 如果话题不存在，则创建新的话题
                            topic = new Topics();
                            topic.setName(topicName);
                            topicMapper.insert(topic);
                        }
                        return topic;
                    })
                    .collect(Collectors.toList());

            // 将帖子和话题建立关联，并存储到数据库
            for (Topics topic : topics) {
                PostTopic postTopics = new PostTopic();
                postTopics.setPostId(post.getId()); // 现在可以正常获取到 post 的 id
                postTopics.setTopicId(topic.getId());
                System.out.println("插入中："+post.getId()+topic.getId());
                postTopicMapper.insert(postTopics);
            }
        }

        return Result.ok(convertToPostDTO(post));
    }



    @Override
    public Result addComment(Long postId, Comment comment) {
        Post post = postsMapper.selectById(postId);
        if (post == null) {
            return Result.fail("Post not found");
        }
        User author = userMapper.selectById(comment.getAuthorId());
        if (author == null) {
            return Result.fail("User not found");
        }
        comment.setAuthorId(author.getId());
        comment.setPostId(post.getId());

        commentsMapper.insert(comment);
        return Result.ok(comment);
    }

    @Override
    public Result likeComment(Long postId, Long commentId) {
        // Find the comment by its id
        Comment comment = commentsMapper.selectById(commentId);
        if (comment == null) {
            return Result.fail("Comment not found");
        }

        // Increment the like count
        comment.setLikeCount(comment.getLikeCount() + 1);

        // Save the updated comment
        commentsMapper.updateById(comment);

        return Result.ok("Comment liked successfully");
    }

    @Override
    public Result addNestedComment(Long postId, Long parentCommentId, Comment comment) {
        // Check if the post exists
        Post post = postsMapper.selectById(postId);
        if (post == null) {
            return Result.fail("Post not found");
        }

        // Set the postId and parentCommentId for the nested comment
        comment.setPostId(postId);
        comment.setParentCommentId(parentCommentId);
        Console.log("嵌套评论的内容是" + comment.getContent());

        // Save the nested comment
        commentsMapper.insert(comment);

        return Result.ok("Nested comment added successfully");
    }

    @Override
    public Result getComments(Long postId) {
        try {
            List<Comment> comments = commentsMapper.getCommentsByPostId(postId);

            // 将Comment对象转换为CommentDTO对象
            List<CommentDTO> commentDTOs = comments.stream()
                    .map(comment -> {
                        CommentDTO dto = new CommentDTO();
                        dto.setId(String.valueOf(comment.getId())); // 将id转换为String类型
                        dto.setContent(comment.getContent());
                        dto.setPostId(comment.getPostId());
                        dto.setAuthorId(comment.getAuthorId());
                        dto.setLikeCount(comment.getLikeCount());
                        dto.setCreatedAt(comment.getCreatedAt());
                        dto.setParentCommentId(String.valueOf(comment.getParentCommentId()));
                        return dto;
                    })
                    .collect(Collectors.toList());

            return Result.ok(commentDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "服务器异常", null, null);
        }
    }

    @Override
    public Result likePost(Long postId) {
        Post post = postsMapper.selectById(postId);
        if (post == null) {
            return Result.fail("Post not found");
        }

        post.setLikeCount(post.getLikeCount() + 1);
        postsMapper.updateById(post);
        return Result.ok();
    }

    @Override
    public Result searchPosts(String keyword) {
        List<Post> posts = postsMapper.searchByKeyword(keyword);
        List<PostDTO> postDTOs = posts.stream().map(this::convertToPostDTO).collect(Collectors.toList());
        return Result.ok(postDTOs, (long) postDTOs.size());
    }

    private PostDTO convertToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId().toString());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setViewCount(post.getViewCount());
        postDTO.setLikeCount(post.getLikeCount());
        postDTO.setCreatedAt(post.getCreatedAt());

        UserDTO authorDTO = new UserDTO();
        authorDTO.setId(post.getAuthorId());
        User author = userMapper.selectById(post.getAuthorId());
        authorDTO.setNickName(author.getNickName());
        authorDTO.setEmail(author.getEmail());
        postDTO.setAuthorId(authorDTO.getId());

        // 查询帖子相关的话题名称列表
        List<String> topicNames = postTopicMapper.selectTopicNamesByPostId(post.getId());

        // 设置话题名称列表到 postDTO
        if (topicNames != null && !topicNames.isEmpty()) {
            postDTO.setTopics(topicNames);
        }

        return postDTO;
    }


}
