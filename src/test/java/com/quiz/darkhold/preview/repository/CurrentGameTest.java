package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.preview.model.PublishInfo;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CurrentGameTest {
    private final CurrentGame currentGame = new CurrentGame();
    private final NitriteCollection collection = Mockito.mock(NitriteCollection.class);

    @BeforeEach
    public void setup() {
        Whitebox.setInternalState(currentGame, "collection", collection);
    }

    @Test
    public void saveCurrentStatus() {
        PublishInfo publishInfo = new PublishInfo();
        publishInfo.setPin("1234");
        publishInfo.setModerator("admin");
        Assertions.assertAll(() -> currentGame.saveCurrentStatus(publishInfo));
    }

    @Test
    public void getActiveUsersInGame() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        List<String> users = currentGame.getActiveUsersInGame("1234");
        Assertions.assertAll("name",
                () -> Assertions.assertEquals("admin",
                        users.get(0)
                ),
                () -> Assertions.assertEquals("tester",
                        users.get(1)
                )
        );
    }

    @Test
    public void saveUserToActiveGame() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertAll(() -> currentGame.saveUserToActiveGame("1234", "admin"));
    }

    @Test
    public void saveQuestionsToActiveGame() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        List<QuestionSet> questionSets = new ArrayList<>();
        Assertions.assertAll(() -> currentGame.saveQuestionsToActiveGame("1234", questionSets));
    }

    @Test
    public void getCurrentQuestionNo() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertEquals(5, currentGame.getCurrentQuestionNo("1234"));
    }

    @Test
    public void getQuestionsOnAPin() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        List<QuestionSet> questionSets = currentGame.getQuestionsOnAPin("1234");
        Assertions.assertEquals("Q1", questionSets.get(0).getQuestion());
    }

    @Test
    public void incrementQuestionCount() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertAll(() -> currentGame.incrementQuestionCount("1234"));
    }

    @Test
    public void findModerator() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertEquals("admin", currentGame.findModerator("1234"));
    }

    @Test
    public void saveCurrentScore() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Assertions.assertAll(() -> currentGame.saveCurrentScore("1234", "admin", 1));
        Assertions.assertAll(() -> currentGame.saveCurrentScore("1234", "test", 1));
    }

    @Test
    public void getCurrentScore() {
        when(collection.find(ArgumentMatchers.any(Filter.class))).thenReturn(new MockCursor());
        Map<String, Integer> scores = currentGame.getCurrentScore("1234");
        Assertions.assertEquals(5, scores.get("admin"));
    }

}