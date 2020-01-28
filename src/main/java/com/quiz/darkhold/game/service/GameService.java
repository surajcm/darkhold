package com.quiz.darkhold.game.service;

import com.quiz.darkhold.preview.repository.CurrentGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private CurrentGame currentGame;

    public List<String> saveAndGetAllParticipants(String pin, String userName) {
        currentGame.saveUserToActiveGame(pin, userName);
        return currentGame.getActiveUsersInGame(pin);
    }

    public List<String> getAllParticipants(String pin) {
        return currentGame.getActiveUsersInGame(pin);
    }

    public int getCurrentQuestionNo(String pin) {
        return currentGame.getCurrentQuestionNo(pin);
    }
}
