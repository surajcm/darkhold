package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.dto.QuestionRequest;
import com.quiz.darkhold.challenge.dto.QuestionResponse;
import com.quiz.darkhold.challenge.dto.ReorderRequest;
import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.challenge.repository.QuestionSetRepository;
import com.quiz.darkhold.user.entity.DarkholdUserDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    private final Logger logger = LogManager.getLogger(QuestionService.class);
    private final QuestionSetRepository questionSetRepository;
    private final ChallengeRepository challengeRepository;

    public QuestionService(final QuestionSetRepository questionSetRepository,
                           final ChallengeRepository challengeRepository) {
        this.questionSetRepository = questionSetRepository;
        this.challengeRepository = challengeRepository;
    }

    @Transactional
    public QuestionResponse createQuestion(final QuestionRequest request) {
        var challenge = getOwnedChallenge(request.getChallengeId());
        if (challenge == null) {
            return null;
        }
        var questionSet = buildQuestionSet(request, challenge);
        questionSet.setDisplayOrder(getNextDisplayOrder(challenge));
        var saved = questionSetRepository.save(questionSet);
        logger.info("Created question {} for challenge {}", saved.getId(), request.getChallengeId());
        return QuestionResponse.fromEntity(saved);
    }

    @Transactional
    public QuestionResponse updateQuestion(final Long questionId, final QuestionRequest request) {
        var question = getOwnedQuestion(questionId);
        if (question == null) {
            return null;
        }
        updateQuestionFields(question, request);
        var saved = questionSetRepository.save(question);
        logger.info("Updated question {}", questionId);
        return QuestionResponse.fromEntity(saved);
    }

    @Transactional
    public boolean deleteQuestion(final Long questionId) {
        var question = getOwnedQuestion(questionId);
        if (question == null) {
            return false;
        }
        questionSetRepository.delete(question);
        logger.info("Deleted question {}", questionId);
        return true;
    }

    public QuestionResponse getQuestion(final Long questionId) {
        var question = getOwnedQuestion(questionId);
        return question == null ? null : QuestionResponse.fromEntity(question);
    }

    @Transactional
    public boolean reorderQuestions(final ReorderRequest request) {
        var challenge = getOwnedChallenge(request.getChallengeId());
        if (challenge == null) {
            return false;
        }
        updateQuestionOrders(request.getChallengeId(), request.getQuestionIds());
        logger.info("Reordered {} questions", request.getQuestionIds().size());
        return true;
    }

    @Transactional
    public QuestionResponse duplicateQuestion(final Long questionId) {
        var original = getOwnedQuestion(questionId);
        if (original == null) {
            return null;
        }
        var newQuestion = copyQuestion(original);
        var saved = questionSetRepository.save(newQuestion);
        logger.info("Duplicated question {} as {}", questionId, saved.getId());
        return QuestionResponse.fromEntity(saved);
    }

    @Transactional
    public int bulkDeleteQuestions(final List<Long> questionIds) {
        int deleted = 0;
        for (var questionId : questionIds) {
            if (deleteQuestion(questionId)) {
                deleted++;
            }
        }
        logger.info("Bulk deleted {} questions", deleted);
        return deleted;
    }

    private Challenge getOwnedChallenge(final Long challengeId) {
        var challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty() || !isOwner(challengeOpt.get())) {
            logger.warn("Challenge {} not found or not owned", challengeId);
            return null;
        }
        return challengeOpt.get();
    }

    private QuestionSet getOwnedQuestion(final Long questionId) {
        var questionOpt = questionSetRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            logger.warn("Question not found: {}", questionId);
            return null;
        }
        var question = questionOpt.get();
        if (question.getChallenge() == null || !isOwner(question.getChallenge())) {
            logger.warn("Question {} not owned by user", questionId);
            return null;
        }
        return question;
    }

    private boolean isOwner(final Challenge challenge) {
        return challenge.getChallengeOwner().equals(currentUserId());
    }

    private int getNextDisplayOrder(final Challenge challenge) {
        if (challenge.getQuestionSets() == null || challenge.getQuestionSets().isEmpty()) {
            return 0;
        }
        return challenge.getQuestionSets().stream()
                .mapToInt(q -> q.getDisplayOrder() != null ? q.getDisplayOrder() : 0)
                .max().orElse(0) + 1;
    }

    private QuestionSet buildQuestionSet(final QuestionRequest request, final Challenge challenge) {
        var qs = new QuestionSet();
        qs.setQuestion(request.getQuestion());
        qs.setAnswer1(request.getAnswer1());
        qs.setAnswer2(request.getAnswer2());
        qs.setAnswer3(request.getAnswer3());
        qs.setAnswer4(request.getAnswer4());
        qs.setCorrectOptions(request.getCorrectOptions());
        qs.setQuestionType(request.getQuestionType());
        qs.setTimeLimit(request.getTimeLimit());
        qs.setPoints(request.getPoints());
        qs.setAcceptableAnswers(request.getAcceptableAnswers());
        qs.setChallenge(challenge);
        return qs;
    }

    private void updateQuestionFields(final QuestionSet question, final QuestionRequest request) {
        question.setQuestion(request.getQuestion());
        question.setAnswer1(request.getAnswer1());
        question.setAnswer2(request.getAnswer2());
        question.setAnswer3(request.getAnswer3());
        question.setAnswer4(request.getAnswer4());
        question.setCorrectOptions(request.getCorrectOptions());
        question.setQuestionType(request.getQuestionType());
        question.setTimeLimit(request.getTimeLimit());
        question.setPoints(request.getPoints());
        question.setAcceptableAnswers(request.getAcceptableAnswers());
    }

    private void updateQuestionOrders(final Long challengeId, final List<Long> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            var questionOpt = questionSetRepository.findById(questionIds.get(i));
            if (questionOpt.isPresent() && belongsToChallenge(questionOpt.get(), challengeId)) {
                questionOpt.get().setDisplayOrder(i);
                questionSetRepository.save(questionOpt.get());
            }
        }
    }

    private boolean belongsToChallenge(final QuestionSet question, final Long challengeId) {
        return question.getChallenge() != null && question.getChallenge().getId().equals(challengeId);
    }

    private QuestionSet copyQuestion(final QuestionSet original) {
        var newQuestion = new QuestionSet();
        copyBasicFields(newQuestion, original);
        copyExtendedFields(newQuestion, original);
        newQuestion.setDisplayOrder(getNextDisplayOrder(original.getChallenge()));
        newQuestion.setChallenge(original.getChallenge());
        return newQuestion;
    }

    private void copyBasicFields(final QuestionSet dest, final QuestionSet src) {
        dest.setQuestion(src.getQuestion());
        dest.setAnswer1(src.getAnswer1());
        dest.setAnswer2(src.getAnswer2());
        dest.setAnswer3(src.getAnswer3());
        dest.setAnswer4(src.getAnswer4());
        dest.setCorrectOptions(src.getCorrectOptions());
    }

    private void copyExtendedFields(final QuestionSet dest, final QuestionSet src) {
        dest.setQuestionType(src.getQuestionType());
        dest.setTimeLimit(src.getTimeLimit());
        dest.setPoints(src.getPoints());
        dest.setAcceptableAnswers(src.getAcceptableAnswers());
    }

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal();
        return ((DarkholdUserDetails) principal).getUser().getId();
    }
}
