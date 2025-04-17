package com.IGsystem.service.Imp;

import com.IGsystem.dto.UserQuestionDTO;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.TopicsDTO;
import com.IGsystem.dto.UserDTO;
import com.IGsystem.entity.*;
import com.IGsystem.mapper.*;
import com.IGsystem.service.PostService;
import com.IGsystem.service.UserQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserQuestionServiceImp extends ServiceImpl<userQuestionMapper, userQuestion> implements UserQuestionService {
    @Autowired
    private userQuestionMapper userQuestion;

    @Autowired
    private CommentsQuestionMapper commentsQuestion;

    @Autowired
    private QuestionLikeMapper questionLikeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private userQuestionTopicMapper questionTopic;

    @Override
    public Result getAllQuestions() {
        List<userQuestion> posts = userQuestion.selectList(null);
        List<UserQuestionDTO> userQuestionDTOs = posts.stream().map(this::convertToUserQuestionDTO).collect(Collectors.toList());
        return Result.ok(userQuestionDTOs, (long) userQuestionDTOs.size());
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
    public Result getQuestionById(Long id) {
        userQuestion question = userQuestion.selectById(id);
        if (question == null) {
            return Result.fail("Post not found");
        }
        return Result.ok(convertToUserQuestionDTO(question));
    }

    @Override
    @Transactional
    public Result createQuestion(UserQuestionDTO userQuestionDTO) {
        userQuestion question = new userQuestion();
        question.setTitle(userQuestionDTO.getTitle());
        question.setContent(userQuestionDTO.getContent());

        User author = userMapper.selectById(userQuestionDTO.getAuthorId());
        if (author == null) {
            return Result.fail("User not found");
        }
        question.setAuthorId(author.getId());

        // 插入帖子到数据库
        userQuestion.insert(question);

        // 处理话题名称列表
        if (userQuestionDTO.getTopics() != null && !userQuestionDTO.getTopics().isEmpty()) {
            List<Topics> topics = userQuestionDTO.getTopics().stream()
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
                userQuestionTopic questionTopic1 = new userQuestionTopic();
                questionTopic1.setQuestionId(question.getId()); // 现在可以正常获取到 question 的 id
                questionTopic1.setTopicId(topic.getId());
                System.out.println("插入中："+question.getId()+topic.getId());
                questionTopic.insert(questionTopic1);
            }
        }

        return Result.ok(convertToUserQuestionDTO(question));
    }



    @Override
    public Result addComment(Long questionId, commentQuestion comment) {
        userQuestion question = userQuestion.selectById(questionId);
        if (question == null) {
            return Result.fail("Post not found");
        }
        User author = userMapper.selectById(comment.getAuthorId());
        if (author == null) {
            return Result.fail("User not found");
        }
        comment.setAuthorId(author.getId());
        comment.setQuestionId(question.getId());

        commentsQuestion.insert(comment);
        return Result.ok(comment);
    }

    @Override
    public Result likeComment(Long questionId, Long commentId) {
        // Find the comment by its id
        commentQuestion comment = commentsQuestion.selectById(commentId);
        if (comment == null) {
            return Result.fail("Comment not found");
        }

        // Increment the like count
        comment.setLikeCount(comment.getLikeCount() + 1);

        // Save the updated comment
        commentsQuestion.updateById(comment);

        return Result.ok("Comment liked successfully");
    }

    @Override
    public Result addNestedComment(Long questionId, Long parentCommentId, commentQuestion comment) {
        // Check if the post exists
        userQuestion question = userQuestion.selectById(questionId);
        if (question == null) {
            return Result.fail("Post not found");
        }

        // Set the postId and parentCommentId for the nested comment
        comment.setQuestionId(questionId);
        comment.setParentCommentId(parentCommentId);

        // Save the nested comment
        commentsQuestion.insert(comment);

        return Result.ok("Nested comment added successfully");
    }

    @Override
    public Result getComments(Long questionId) {
        try {
            List<commentQuestion> commentQuestions = commentsQuestion.getCommentsByQuestionId(questionId);
            return Result.ok(commentQuestions);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "服务器异常", null, null);
        }
    }

    @Override
    public Result likeQuestion(Long questionId) {
        userQuestion question = userQuestion.selectById(questionId);
        if (question == null) {
            return Result.fail("Post not found");
        }

        question.setLikeCount(question.getLikeCount() + 1);
        userQuestion.updateById(question);
        return Result.ok();
    }

    @Override
    public Result searchQuestions(String keyword) {
        List<userQuestion> questions = userQuestion.searchByKeyword(keyword);
        List<UserQuestionDTO> userQuestionDTOs = questions.stream().map(this::convertToUserQuestionDTO).collect(Collectors.toList());
        return Result.ok(userQuestionDTOs, (long) userQuestionDTOs.size());
    }

    private UserQuestionDTO convertToUserQuestionDTO(userQuestion question) {
        UserQuestionDTO userQuestionDTO = new UserQuestionDTO();
        userQuestionDTO.setId(question.getId().toString());
        userQuestionDTO.setTitle(question.getTitle());
        userQuestionDTO.setContent(question.getContent());
        userQuestionDTO.setViewCount(question.getViewCount());
        userQuestionDTO.setLikeCount(question.getLikeCount());
        userQuestionDTO.setCreatedAt(question.getCreatedAt());

        UserDTO authorDTO = new UserDTO();
        authorDTO.setId(question.getAuthorId());
        User author = userMapper.selectById(question.getAuthorId());
        authorDTO.setNickName(author.getNickName());
        authorDTO.setEmail(author.getEmail());
        userQuestionDTO.setAuthorId(authorDTO.getId());

        // 查询帖子相关的话题名称列表
        List<String> topicNames = questionTopic.selectTopicNamesByQuestionId(question.getId());

        // 设置话题名称列表到 userQuestionDTO
        if (topicNames != null && !topicNames.isEmpty()) {
            userQuestionDTO.setTopics(topicNames);
        }

        return userQuestionDTO;
    }
}
