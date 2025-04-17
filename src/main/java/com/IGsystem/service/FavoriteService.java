package com.IGsystem.service;


import com.IGsystem.dto.Result;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface FavoriteService {
    Result getUserFavoriteFolders();
    Result saveFavoriteQuestion(int favorite_id,int question_id, String question_type, long userId);
    Result removeFolder(@RequestBody Map<String, String> requestBody);
}
