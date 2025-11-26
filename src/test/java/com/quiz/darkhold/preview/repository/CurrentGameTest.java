package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.preview.model.PublishInfo;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentGameTest {
    @Mock
    private NitriteCollection collection;

    @InjectMocks
    private CurrentGame currentGame;

    @Test
    void saveCurrentStatus() {
        var publishInfo = new PublishInfo();
        publishInfo.setPin("1234");
        publishInfo.setModerator("admin");
        Assertions.assertAll(() -> currentGame.saveCurrentStatus(publishInfo, new ArrayDeque<>()));
    }

    @Test
    void getActiveUsersInGame() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        var users = currentGame.getActiveUsersInGame("1234");
        Assertions.assertAll("name",
                () -> Assertions.assertEquals("admin",
                        users.getFirst()
                ),
                () -> Assertions.assertEquals("tester",
                        users.get(1)
                )
        );
    }

    @Test
    void saveUserToActiveGame() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertAll(() -> currentGame.saveUserToActiveGame("1234", "admin"));
    }

    @Test
    void saveQuestionsToActiveGame() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        List<QuestionSet> questionSets = new ArrayList<>();
        Assertions.assertAll(() -> currentGame.saveQuestionsToActiveGame("1234", questionSets));
    }

    @Test
    void getCurrentQuestionNo() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertEquals(5, currentGame.getCurrentQuestionNo("1234"));
    }

    @Test
    void getQuestionsOnAPin() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        var questionSets = currentGame.getQuestionsOnAPin("1234");
        Assertions.assertEquals("Q1", questionSets.getFirst().getQuestion());
    }

    @Test
    void incrementQuestionCount() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertAll(() -> currentGame.incrementQuestionCount("1234"));
    }

    @Test
    void findModerator() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertEquals("admin", currentGame.findModerator("1234"));
    }

    @Test
    void saveCurrentScore() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertAll(() -> currentGame.saveCurrentScore("1234", "admin", 1));
        Assertions.assertAll(() -> currentGame.saveCurrentScore("1234", "test", 1));
    }

    @Test
    void getCurrentScore() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        var scores = currentGame.getCurrentScore("1234");
        Assertions.assertEquals(5, scores.get("admin"));
    }

}
