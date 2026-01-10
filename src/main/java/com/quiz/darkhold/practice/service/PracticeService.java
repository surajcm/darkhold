package com.quiz.darkhold.practice.service;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.model.GameMode;
import com.quiz.darkhold.preview.entity.CurrentGameSession;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGameSessionRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling practice/single-player game mode.
 */
@Service
public class PracticeService {

    private final Logger logger = LogManager.getLogger(PracticeService.class);

    private final ChallengeRepository challengeRepository;
    private final CurrentGameSessionRepository sessionRepository;

    public PracticeService(final ChallengeRepository challengeRepository,
                          final CurrentGameSessionRepository sessionRepository) {
        this.challengeRepository = challengeRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Initialize a practice game session.
     *
     * @param challengeId the challenge to practice
     * @param playerName  the player's name
     * @param session     HTTP session to store the practice game identifier
     * @return PublishInfo with practice game details
     */
    public PublishInfo initializePracticeGame(final String challengeId,
                                             final String playerName,
                                             final HttpSession session) {
        logger.info("Initializing practice game for challenge: {} player: {}", challengeId, playerName);

        // Generate internal identifier (UUID-based, session-only)
        String practiceId = "PRACTICE-" + UUID.randomUUID().toString().substring(0, 8);

        // Fetch challenge questions
        var challengeIdLong = Long.valueOf(challengeId);
        var challenge = challengeRepository.findById(challengeIdLong)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));

        List<QuestionSet> questionsList = new ArrayList<>(challenge.getQuestionSets());

        // Create practice game session
        CurrentGameSession gameSession = new CurrentGameSession(practiceId, playerName);
        gameSession.setGameMode(GameMode.PRACTICE);

        // In practice mode, player is both moderator and participant
        List<String> users = new ArrayList<>();
        users.add(playerName);
        gameSession.setUsersList(users);

        gameSession.setQuestionsList(questionsList);
        gameSession.setCurrentQuestionNo(0);
        gameSession.setScoresMap(new HashMap<>());
        gameSession.setStreakMap(new HashMap<>());
        gameSession.setGameStatus(com.quiz.darkhold.game.model.GameStatus.ACTIVE);

        // Save to database
        sessionRepository.save(gameSession);

        // Store practice ID in session (treated as "gamePin" for compatibility)
        session.setAttribute("gamePin", practiceId);

        logger.info("Practice game initialized with ID: {}", practiceId);

        // Return publish info
        PublishInfo publishInfo = new PublishInfo();
        publishInfo.setPin(practiceId);
        publishInfo.setModerator(playerName);

        return publishInfo;
    }
}
