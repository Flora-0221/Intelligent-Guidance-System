package com.IGsystem.service.Imp;
import com.IGsystem.dto.*;
import com.IGsystem.mapper.QuestionMapper;
import com.IGsystem.mapper.SAQuestionMapper;
import com.IGsystem.service.QuestionService;
import com.IGsystem.utils.IntegerListConvert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

@Service
@Slf4j
public class QuestionServiceImp extends ServiceImpl<QuestionMapper,Question> implements QuestionService {

    @Value("${IG.QuestionImgPath}")
    private String questionImgPath;

    @Autowired
    private SAQuestionMapper saQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Result get() {
        //TODO 获得问题
        //创建随机抽取的wrapper
         QueryWrapper<Question> wrapper = new QueryWrapper<>();
         wrapper.last("LIMIT 5");
         List<Question> questions = baseMapper.selectList(wrapper);
         // 返回结果
         if (!questions.isEmpty()) {
             return Result.ok(questions);
         } else {
             return Result.fail("没有找到问题");
         }
    }

    @Override
    public Result getQuestion(String task, String grade, String subject, String topic, String category,int questionCount) {
        // 创建查询条件的wrapper
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        // 根据请求参数设置查询条件
        if (!"All".equals(task)) {
            System.out.println("题型"+task);
            if ("short answer questions".equals(task)) {
                QueryWrapper<SAQuestion> saWrapper = new QueryWrapper<>();
                String randomQuery = "ORDER BY RAND() LIMIT " + questionCount;
                saWrapper.last(randomQuery);  // 使用SAQuestion的wrapper设置随机抽取条件
                List<SAQuestion> selectedQuestions = saQuestionMapper.selectList(saWrapper);  // 执行随机抽取
                // 处理返回结果
                if (!selectedQuestions.isEmpty()) {
                    return Result.ok(selectedQuestions);
                } else {
                    return Result.fail("没有找到Short Answer问题");
                }
            }else {
                wrapper.eq("task", task);
            }

        }
        if (!"All".equals(grade)) {
            wrapper.eq("grade", grade);
        }
        if (!"All".equals(subject)) {
            wrapper.eq("subject", subject);
        }
        if (!"All".equals(topic)) {
            wrapper.eq("topic", topic);
        }
        if (!"All".equals(category)) {
            wrapper.eq("category", category);
        }

        // 限制返回的问题数量
        wrapper.last("LIMIT " + questionCount);

        // 从数据库中获取符合条件的问题
        List<Question> questions = baseMapper.selectList(wrapper);

        // 返回结果
        if (!questions.isEmpty()) {
            return Result.ok(questions);
        } else {
            return Result.fail("没有找到问题");
        }
    }

    @Override
    public Result getImage(String split, Long id) {
        try {
//            File file = new File("E:\\dataset\\"+split+"\\"+id+"\\image.png");
            File file = new File(questionImgPath+split+"\\"+id+"\\image.png");
//            File file = new File("/home/image/" + split + "/" + id + "/image.png");
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Result.ok(Base64.getEncoder().encodeToString(fileContent));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("题目图像获取失败");
        }
    }

    @Override
    public Result getQuestionByID(String questionids) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        List<Integer> questionIDs = IntegerListConvert.convertStringToIntegerList(questionids);
        wrapper.in("id", questionIDs);
        List<Question> questions = baseMapper.selectList(wrapper);
        return Result.ok(questions);
    }

    @Override
    public Result getByLabel(String grade, String subject, String task, String category, String topic) {
        return Result.ok(questionMapper.selectByLabels(
                ("All".equals(grade) ? null : grade),
                ("All".equals(subject) ? null : subject),
                ("All".equals(task) ? null : task),
                ("All".equals(category) ? null : category),
                ("All".equals(topic) ? null : topic)
        ));
    }

//    @Override
//    public Result getLabel(String grade, String subject, String task, String category, String topic) {
//        List<Question> queryResults = questionMapper.selectLabels(
//                ("All".equals(grade) ? null : grade),
//                ("All".equals(subject) ? null : subject),
//                ("All".equals(task) ? null : task),
//                ("All".equals(category) ? null : category),
//                ("All".equals(topic) ? null : topic)
//        );
//        Map<String, Set<String>> mergedValues = new HashMap<>();
//
//        for (Question result : queryResults) {
//            String gradeValue = result.getGrade();
//            String subjectValue = result.getSubject();
//            String taskValue = result.getTask();
//            String categoryValue = result.getCategory();
//            String topicValue = result.getTopic();
//
//            // Merge the values for each key into a set
//            mergedValues.computeIfAbsent("grade", k -> new HashSet<>()).add(gradeValue);
//            mergedValues.computeIfAbsent("subject", k -> new HashSet<>()).add(subjectValue);
//            mergedValues.computeIfAbsent("task", k -> new HashSet<>()).add(taskValue);
//            mergedValues.computeIfAbsent("category", k -> new HashSet<>()).add(categoryValue);
//            mergedValues.computeIfAbsent("topic", k -> new HashSet<>()).add(topicValue);
//        }
//
//
//        // Convert sets to lists for the desired output format
//        Map<String, List<String>> output = new HashMap<>();
//        mergedValues.forEach((key, valueSet) -> output.put(key, new ArrayList<>(valueSet)));
//
//        return Result.ok(output);
//    }

@Override
public Result getLabel(String[] grades, String[] subjects, String[] tasks, String[] categories, String[] topics) {
    Map<String, Set<String>> mergedValues = new HashMap<>();

    for (String grade : grades) {
        for (String subject : subjects) {
            for (String task : tasks) {
                for (String category : categories) {
                    for (String topic : topics) {
                        List<Question> queryResults = questionMapper.selectLabels(
                                ("All".equals(grade) ? null : grade),
                                ("All".equals(subject) ? null : subject),
                                ("All".equals(task) ? null : task),
                                ("All".equals(category) ? null : category),
                                ("All".equals(topic) ? null : topic)
                        );

                        for (Question result : queryResults) {
                            String gradeValue = result.getGrade();
                            String subjectValue = result.getSubject();
                            String taskValue = result.getTask();
                            String categoryValue = result.getCategory();
                            String topicValue = result.getTopic();

                            // Merge the values for each key into a set
                            mergedValues.computeIfAbsent("grade", k -> new HashSet<>()).add(gradeValue);
                            mergedValues.computeIfAbsent("subject", k -> new HashSet<>()).add(subjectValue);
                            mergedValues.computeIfAbsent("task", k -> new HashSet<>()).add(taskValue);
                            mergedValues.computeIfAbsent("category", k -> new HashSet<>()).add(categoryValue);
                            mergedValues.computeIfAbsent("topic", k -> new HashSet<>()).add(topicValue);
                        }
                    }
                }
            }
        }
    }

    // Convert sets to lists for the desired output format
    Map<String, List<String>> output = new HashMap<>();
    mergedValues.forEach((key, valueSet) -> output.put(key, new ArrayList<>(valueSet)));
    if (output.isEmpty()){
        System.out.println(output);
        return Result.fail("No such question");
    }else {
        return Result.ok(output);
    }

}

    @Override
    public Result getQuestionSkill() {
        // 查询字段为 skill 的不重复列表
        return Result.ok(questionMapper.selectDistinctSkills());
    }


}
