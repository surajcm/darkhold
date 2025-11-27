package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.preview.entity.CurrentGameSession;
import com.quiz.darkhold.preview.model.PublishInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CurrentGame {

    private final Logger logger = LogManager.getLogger(CurrentGame.class);

    @Autowired
    private CurrentGameSessionRepository repository;

    /**
     * save the game info to H2 database before we start the game.
     *
     * @param publishInfo publish info
     */
    public void saveCurrentStatus(final PublishInfo publishInfo, final ArrayDeque<QuestionSet> questionSets) {
        List<String> users = new ArrayList<>();
        users.add(publishInfo.getModerator());

        CurrentGameSession session = new CurrentGameSession(publishInfo.getPin(), publishInfo.getModerator());
        session.setUsersList(users);
        session.setQuestionsList(new ArrayList<>(questionSets));
        session.setCurrentQuestionNo(0);
        session.setScoresMap(new HashMap<>());

        repository.save(session);
    }

    /**
     * get the active users in the game.
     *
     * @param pin of the game
     * @return users
     */
    public List<String> getActiveUsersInGame(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            List<String> users = session.get().getUsersList();
            logger.info("Participants are : {}", users);
            return users;
        }
        return new ArrayList<>();
    }

    /**
     * save user to active game.
     *
     * @param pin      of the game
     * @param userName of user
     */
    public void saveUserToActiveGame(final String pin, final String userName) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            List<String> users = gameSession.getUsersList();
            if (users == null) {
                users = new ArrayList<>();
            }
            users.add(userName);
            gameSession.setUsersList(users);
            repository.save(gameSession);
        }
    }

    /**
     * save questions to active game.
     *
     * @param pin          of the game
     * @param questionSets of the game
     */
    public void saveQuestionsToActiveGame(final String pin, final List<QuestionSet> questionSets) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            List<QuestionSet> questions = gameSession.getQuestionsList();
            if (questions == null) {
                questions = new ArrayList<>();
            }
            questions.addAll(questionSets);
            gameSession.setQuestionsList(questions);
            repository.save(gameSession);
        }
    }

    /**
     * current question no.
     *
     * @param pin of game
     * @return question no
     */
    public int getCurrentQuestionNo(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            Integer questionNo = session.get().getCurrentQuestionNo();
            logger.info("getCurrentQuestionNo : questionNo : {}}", questionNo);
            return questionNo != null ? questionNo : 0;
        }
        return 0;
    }

    /**
     * get all questions of the pin.
     *
     * @param pin of game
     * @return question list
     */
    public List<QuestionSet> getQuestionsOnAPin(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            List<QuestionSet> questions = session.get().getQuestionsList();
            if (questions != null) {
                logger.info("question count : {}", questions.size());
                return questions;
            }
        }
        return new ArrayList<>();
    }

    /**
     * points to the next question.
     *
     * @param pin of game
     */
    public void incrementQuestionCount(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Integer questionNo = gameSession.getCurrentQuestionNo();
            if (questionNo != null) {
                gameSession.setCurrentQuestionNo(questionNo + 1);
                repository.save(gameSession);
            }
        }
    }

    /**
     * find the moderator.
     *
     * @param pin pin
     * @return moderator
     */
    public String findModerator(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        return session.map(CurrentGameSession::getModerator).orElse(null);
    }

    /**
     * save current score to H2 database.
     *
     * @param pin    pin of game
     * @param name   of user
     * @param status success or not
     */
    public void saveCurrentScore(final String pin, final String name, final Integer status) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Map<String, Integer> scores = gameSession.getScoresMap();
            if (scores == null) {
                scores = new HashMap<>();
            }
            if (scores.containsKey(name)) {
                scores.compute(name, (k, currentValue) -> currentValue + status);
            } else {
                scores.put(name, status);
            }
            gameSession.setScoresMap(scores);
            repository.save(gameSession);
        }
    }

    public Map<String, Integer> getCurrentScore(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            Map<String, Integer> scores = session.get().getScoresMap();
            return scores != null ? scores : new HashMap<>();
        }
        return new HashMap<>();
    }

    public QuestionPointer getCurrentQuestionPointer(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        QuestionPointer questionPointer = new QuestionPointer();

        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Integer questionNo = gameSession.getCurrentQuestionNo();
            List<QuestionSet> questions = gameSession.getQuestionsList();

            if (questionNo != null) {
                logger.info("questionNo : {}", questionNo);
                if (questions != null) {
                    logger.info("questions size : {}", questions.size());
                    questionPointer.setCurrentQuestionNumber(questionNo);
                    questionPointer.setTotalQuestionCount(questions.size());
                    if (questionNo < questions.size()) {
                        questionPointer.setCurrentQuestion(questions.get(questionNo));
                    }
                }
            }
        }
        return questionPointer;
    }

    public void stopTheGame(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        session.ifPresent(currentGameSession -> repository.delete(currentGameSession));
    }
}
