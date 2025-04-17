package com.IGsystem.service.Imp;

import com.IGsystem.dto.Folder;
import com.IGsystem.dto.Question;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.FavoriteQuestions;
import com.IGsystem.mapper.FolderMapper;
import com.IGsystem.mapper.QuestionMapper;
import com.IGsystem.mapper.FavoriteQuestionsMapper;
import com.IGsystem.service.FavoriteService;
import com.IGsystem.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FavoriteServiceImp extends ServiceImpl<FolderMapper, Folder> implements FavoriteService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private FavoriteQuestionsMapper favoriteQuestionMapper;

    @Autowired
    private FolderMapper folderMapper;

    @Autowired
    private FavoriteQuestionsMapper favoriteQuestionsMapper;

//    @Override
//    public Result getUserFavoriteQuestions(){
//        Long userId = UserHolder.getUser().getId();
//        List<Question> questionList = new ArrayList<>();
//        List<Folder> folders = (List<Folder>) getUserFavoriteFolders().getData();
//        for (Folder favorite : folders) {
//            List<FavoriteQuestions> favoriteQuestions = favoriteQuestionsMapper.selectList(
//                    new QueryWrapper<FavoriteQuestions>()
//                            .eq("favorite_id", favorite.getId())
//                            .eq("user_id", userId));
//            for(FavoriteQuestions favoriteQuestion:favoriteQuestions){
//                Integer questionId = favoriteQuestion.getQuestionId();
//                Question question = questionMapper.selectById(questionId);
//                questionList.add(question);
//            }
//        }
//        return Result.ok(questionList);
//    }

    @Override
    public Result getUserFavoriteFolders() {
        Long userId = UserHolder.getUser().getId();
        try {
            // 查询用户收藏夹信息
            List<Folder> favorites = list(new QueryWrapper<Folder>().eq("user_id", userId));
            if (favorites.isEmpty()) {
                return Result.fail("No favorite folders found");
            } else {
                // 遍历每个收藏夹，查询对应的题目列表并组装成新的数据结构返回
                List<Map<String, Object>> data = new ArrayList<>();
                for (Folder favorite : favorites) {
                    List<FavoriteQuestions> favoriteQuestions = favoriteQuestionsMapper.selectList(
                            new QueryWrapper<FavoriteQuestions>()
                                    .eq("favorite_id", favorite.getId())
                                    .eq("user_id", userId));
                    List<Question> questions = new ArrayList<>();
                    for(FavoriteQuestions favoriteQuestion : favoriteQuestions){
                        Integer questionId = favoriteQuestion.getQuestionId();
                        Question question = questionMapper.selectById(questionId);
                        questions.add(question);
                    }
                    Map<String, Object> folderData = new HashMap<>();
                    folderData.put("folder", favorite);
                    folderData.put("questions", questions);
                    data.add(folderData);
                }
                return Result.ok(data);
            }
        } catch (Exception e) {
            return Result.fail("Failed to fetch favorite folders and questions");
        }
    }

    @Override
    public Result saveFavoriteQuestion(int favorite_id,int question_id, String question_type, long userId) {
        FavoriteQuestions favoriteQuestions = new FavoriteQuestions();
        favoriteQuestions.setFavoriteId(favorite_id);
        favoriteQuestions.setQuestionId(question_id);
        favoriteQuestions.setQuestionType(question_type);
        favoriteQuestions.setUserId(userId);
        favoriteQuestionMapper.insert(favoriteQuestions);
        return Result.ok();
    }


    @Override
    public Result removeFolder(@RequestBody Map<String, String> requestBody) {
        String folderId = requestBody.get("folderId");
        Long userId = UserHolder.getUser().getId();
        if (folderId == null || folderId.isEmpty()) {
            return Result.fail("收藏夹ID不能为空");
        }

        Integer id = Integer.valueOf(folderId); // 将字符串转换为整数

        Folder folder = folderMapper.selectById(id);

        if(folder != null){

            int deleteQuestions = favoriteQuestionMapper.delete(new QueryWrapper<FavoriteQuestions>()
                    .eq("favorite_id", folderId)
                    .eq("user_id", userId));
            if(deleteQuestions>=0){
                //删除外键成功
                int result = folderMapper.deleteById(id);
                if (result > 0) {
                    // 删除成功
                    return Result.ok("成功删除收藏夹");
                } else {
                    // 删除失败
                    return Result.fail("删除收藏夹失败");
                }
            }else {
                return Result.fail("删除外键失败");
            }

        }else {
            return Result.fail("未找到收藏夹");
        }
    }
}
