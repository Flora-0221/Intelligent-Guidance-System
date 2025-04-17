package com.IGsystem;

import com.IGsystem.dto.Question;
import com.IGsystem.dto.Result;
import com.IGsystem.mapper.QuestionMapper;
import com.IGsystem.mapper.SAQuestionMapper;
import com.IGsystem.service.Imp.QuestionServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private SAQuestionMapper saQuestionMapper;

    @InjectMocks
    private QuestionServiceImp questionServiceImp;

    private List<Question> mockQuestions;

    @Test
    public void testGet() {
        // 模拟查询结果
        Question question = new Question();
        question.setId(1);
        question.setQuestion("Sample Question?");
        question.setAnswer(1);
        question.setTask("Task1");
        question.setGrade("Grade1");
        question.setSubject("Subject1");
        question.setTopic("Topic1");
        question.setCategory("Category1");

        when(questionMapper.selectList(any())).thenReturn(Arrays.asList(question));

        // 调用get方法
        Result result = questionServiceImp.get();

        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        List<Question> questions = (List<Question>) result.getData();
        assertEquals(1, questions.size());
        assertEquals("Sample Question?", questions.get(0).getQuestion());
    }

    @Test
    public void testGetQuestion() {
        // 模拟查询结果
        Question question = new Question();
        question.setId(1);
        question.setQuestion("Question1");
        question.setAnswer(1);
        question.setTask("Task1");
        question.setGrade("Grade1");
        question.setSubject("Subject1");
        question.setTopic("Topic1");
        question.setCategory("Category1");

        when(questionMapper.selectList(any())).thenReturn(Arrays.asList(question));

        // 调用getQuestion方法
        Result result = questionServiceImp.getQuestion("Task1", "Grade1", "Subject1", "Topic1", "Category1", 5);

        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        List<Question> questions = (List<Question>) result.getData();
        assertEquals(1, questions.size());
        assertEquals("Question1", questions.get(0).getQuestion());
    }


    @Test
    public void testGetByLabel() {
        // Mock label query
        when(questionMapper.selectByLabels(any(), any(), any(), any(), any())).thenReturn(mockQuestions);

        Result result = questionServiceImp.getByLabel("grade 1", "subject 1", "task 1", "category 1", "topic 1");
        assertTrue(result.isOk());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetLabel() {
        // Mock multi-label query
        String[] grades = {"grade 1"};
        String[] subjects = {"subject 1"};
        String[] tasks = {"task 1"};
        String[] categories = {"category 1"};
        String[] topics = {"topic 1"};

        when(questionMapper.selectLabels(any(), any(), any(), any(), any())).thenReturn(mockQuestions);

        Result result = questionServiceImp.getLabel(grades, subjects, tasks, categories, topics);
        assertTrue(result.isOk());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetQuestionByID() {
        String questionIds = "1,2";

        when(questionMapper.selectList(any())).thenReturn(mockQuestions);

        Result result = questionServiceImp.getQuestionByID(questionIds);
        assertTrue(result.isOk());
        assertNotNull(result.getData());
        assertEquals(2, ((List<?>) result.getData()).size());
    }

}
