package com.quiz.darkhold.challenge.controller;

import com.quiz.darkhold.challenge.dto.QuestionRequest;
import com.quiz.darkhold.challenge.dto.QuestionResponse;
import com.quiz.darkhold.challenge.dto.ReorderRequest;
import com.quiz.darkhold.challenge.service.QuestionService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST controller for question CRUD operations.
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final Logger logger = LogManager.getLogger(QuestionController.class);
    private final QuestionService questionService;

    public QuestionController(final QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Create a new question.
     *
     * @param request the question data
     * @return the created question
     */
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody final QuestionRequest request) {
        logger.info("Creating question for challenge: {}", request.getChallengeId());
        var response = questionService.createQuestion(request);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get a question by ID.
     *
     * @param id the question ID
     * @return the question
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable final Long id) {
        logger.info("Getting question: {}", id);
        var response = questionService.getQuestion(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing question.
     *
     * @param id      the question ID
     * @param request the updated question data
     * @return the updated question
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable final Long id,
            @Valid @RequestBody final QuestionRequest request) {
        logger.info("Updating question: {}", id);
        var response = questionService.updateQuestion(id, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a question.
     *
     * @param id the question ID
     * @return success status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteQuestion(@PathVariable final Long id) {
        logger.info("Deleting question: {}", id);
        var deleted = questionService.deleteQuestion(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    /**
     * Duplicate a question within its challenge.
     *
     * @param id the question ID to duplicate
     * @return the new question
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<QuestionResponse> duplicateQuestion(@PathVariable final Long id) {
        logger.info("Duplicating question: {}", id);
        var response = questionService.duplicateQuestion(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Reorder questions within a challenge.
     *
     * @param request the reorder request
     * @return success status
     */
    @PostMapping("/reorder")
    public ResponseEntity<Map<String, Boolean>> reorderQuestions(@Valid @RequestBody final ReorderRequest request) {
        logger.info("Reordering questions in challenge: {}", request.getChallengeId());
        var reordered = questionService.reorderQuestions(request);
        if (!reordered) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(Map.of("reordered", true));
    }

    /**
     * Upload an image for a question.
     *
     * @param id   the question ID
     * @param file the image file
     * @return the image URL
     */
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable final Long id,
            @RequestParam("image") final MultipartFile file) {
        logger.info("Uploading image for question: {}", id);
        String imageUrl = questionService.uploadQuestionImage(id, file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    /**
     * Delete the image for a question.
     *
     * @param id the question ID
     * @return success status
     */
    @DeleteMapping("/{id}/delete-image")
    public ResponseEntity<Map<String, Boolean>> deleteImage(@PathVariable final Long id) {
        logger.info("Deleting image for question: {}", id);
        var deleted = questionService.deleteQuestionImage(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    /**
     * Bulk delete questions.
     *
     * @param questionIds the question IDs to delete
     * @return the number of questions deleted
     */
    @PostMapping("/bulk-delete")
    public ResponseEntity<Map<String, Integer>> bulkDeleteQuestions(@RequestBody final List<Long> questionIds) {
        logger.info("Bulk deleting {} questions", questionIds.size());
        var deleted = questionService.bulkDeleteQuestions(questionIds);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }
}
