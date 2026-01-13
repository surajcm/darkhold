package com.quiz.darkhold.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.darkhold.challenge.dto.ChallengeExportDto;
import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.service.ChallengeService;
import com.quiz.darkhold.util.CommonUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

@Controller
public class ChallengeController {
    private final Logger logger = LogManager.getLogger(ChallengeController.class);
    private final ChallengeService challengeService;

    public record ChallengeWithResponse(Long challengeId, String message) {
    }

    public ChallengeController(final ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping("/options")
    public String options() {
        logger.info("on post to options");
        return "options/options";
    }

    @GetMapping("/options")
    public String optionsGet() {
        logger.info("on get to options");
        return "options/options";
    }

    /**
     * Upload the challenge as a predefined Excel sheet.
     *
     * @param upload             excel file
     * @param title              game title
     * @param description        description
     * @param redirectAttributes redirect attributes
     * @return ResponseEntity with challenge info and appropriate HTTP status
     */
    @PostMapping("/upload_challenge")
    public ResponseEntity<ChallengeWithResponse> handleFileUpload(final MultipartFile upload,
                                                                   final String title,
                                                                   final String description,
                                                                   final RedirectAttributes redirectAttributes) {
        logParams(upload, title, description);
        try {
            var challengeId = challengeService.readProcessAndSaveChallenge(upload, title, description);
            var responseText = "Successfully created " + CommonUtils.sanitizedString(title) + " !!!";
            return ResponseEntity.ok(new ChallengeWithResponse(challengeId, responseText));
        } catch (ChallengeException challengeException) {
            logger.error("Failed to process challenge: {}", challengeException.getErrorMessage());
            return ResponseEntity.badRequest()
                    .body(new ChallengeWithResponse(0L, challengeException.getErrorMessage()));
        }
    }

    private void logParams(final MultipartFile upload,
                           final String title, final String description) {
        logger.info("Received incoming traffic and redirected to upload_pdf");
        var sanitizedTitle = CommonUtils.sanitizedString(title);
        var sanitizedDescription = CommonUtils.sanitizedString(description);
        logger.info("title : {}, description : {} ",
                sanitizedTitle, sanitizedDescription);
        var sanitizedOriginalFileName = CommonUtils.sanitizedString(upload.getOriginalFilename());
        var sanitizedFileSize = CommonUtils.sanitizedString(String.valueOf(upload.getSize()));
        logger.info("File details getOriginalFilename : {}, getSize : {}} ",
                sanitizedOriginalFileName, sanitizedFileSize);
    }

    /**
     * Delete a challenge by its ID.
     *
     * @param challenge          challenge ID to delete
     * @param redirectAttributes redirect attributes
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/delete_challenge")
    public ResponseEntity<Boolean> deleteChallenge(final Long challenge,
                                                   final RedirectAttributes redirectAttributes) {
        var sanitizeChallenge = CommonUtils.sanitizedString(String.valueOf(challenge));
        logger.info("received incoming request to delete_challenge : {}", sanitizeChallenge);
        var deleted = challengeService.deleteChallenge(challenge);
        if (deleted) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Show the unified form to create a new challenge (with or without Excel upload).
     *
     * @return the unified create challenge form view
     */
    @GetMapping("/create_challenge_form")
    public String showCreateChallengeForm() {
        logger.info("Showing unified create challenge form");
        return "challenge/createchallenge";
    }

    /**
     * Save a new empty challenge (without questions).
     *
     * @param title       challenge title
     * @param description challenge description
     * @return ResponseEntity with challenge info
     */
    @PostMapping("/save_challenge")
    public ResponseEntity<ChallengeWithResponse> saveChallenge(
            @RequestParam @NotBlank @Size(max = 100) final String title,
            @RequestParam @Size(max = 250) final String description) {
        var sanitizedTitle = CommonUtils.sanitizedString(title);
        var sanitizedDescription = CommonUtils.sanitizedString(description);
        logger.info("Creating new challenge: title={}, description={}", sanitizedTitle, sanitizedDescription);
        var challenge = challengeService.createEmptyChallenge(title, description);
        return ResponseEntity.ok(new ChallengeWithResponse(challenge.getId(),
                "Successfully created " + sanitizedTitle + " !!!"));
    }

    /**
     * Show the edit form for a challenge.
     *
     * @param id    challenge ID
     * @param model Spring model
     * @return the edit challenge view or redirect to options
     */
    @GetMapping("/edit_challenge/{id}")
    public String showEditChallengeForm(@PathVariable final Long id, final Model model) {
        logger.info("Showing edit form for challenge: {}", id);
        var challenge = challengeService.getChallengeForEdit(id);
        if (challenge == null) {
            logger.warn("Challenge not found or not owned by user: {}", id);
            return "redirect:/options";
        }
        model.addAttribute("challenge", challenge);
        return "challenge/editchallenge";
    }

    /**
     * Update an existing challenge's title and description.
     *
     * @param id          challenge ID
     * @param title       new title
     * @param description new description
     * @return ResponseEntity with updated challenge info
     */
    @PostMapping("/update_challenge/{id}")
    public ResponseEntity<ChallengeWithResponse> updateChallenge(
            @PathVariable final Long id,
            @RequestParam @NotBlank @Size(max = 100) final String title,
            @RequestParam @Size(max = 250) final String description) {
        var sanitizedTitle = CommonUtils.sanitizedString(title);
        logger.info("Updating challenge {}: title={}", id, sanitizedTitle);
        var challenge = challengeService.updateChallenge(id, title, description);
        if (challenge == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ChallengeWithResponse(challenge.getId(),
                "Successfully updated " + sanitizedTitle + " !!!"));
    }

    /**
     * Duplicate a challenge with all its questions.
     *
     * @param id challenge ID to duplicate
     * @return ResponseEntity with new challenge info
     */
    @PostMapping("/duplicate_challenge/{id}")
    public ResponseEntity<ChallengeWithResponse> duplicateChallenge(@PathVariable final Long id) {
        logger.info("Duplicating challenge: {}", id);
        var newChallenge = challengeService.duplicateChallenge(id);
        if (newChallenge == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ChallengeWithResponse(newChallenge.getId(),
                "Successfully duplicated as " + newChallenge.getTitle() + " !!!"));
    }

    /**
     * Export a challenge as JSON.
     */
    @GetMapping("/export_challenge/{id}/json")
    public ResponseEntity<byte[]> exportChallengeAsJson(@PathVariable final Long id) {
        logger.info("Exporting challenge as JSON: {}", id);
        var challenge = challengeService.getChallengeForEdit(id);
        if (challenge == null) {
            return ResponseEntity.notFound().build();
        }
        return buildJsonExport(challenge);
    }

    private ResponseEntity<byte[]> buildJsonExport(
            final com.quiz.darkhold.challenge.entity.Challenge challenge) {
        var dto = ChallengeExportDto.fromEntity(challenge);
        var mapper = new ObjectMapper();
        var filename = challenge.getTitle().replaceAll("[^a-zA-Z0-9]", "_") + ".json";
        try {
            var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(dto);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_JSON).body(json);
        } catch (com.fasterxml.jackson.core.JsonProcessingException jsonException) {
            logger.error("Error exporting challenge: {}", jsonException.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Export a challenge as CSV.
     */
    @GetMapping("/export_challenge/{id}/csv")
    public ResponseEntity<byte[]> exportChallengeAsCsv(@PathVariable final Long id) {
        logger.info("Exporting challenge as CSV: {}", id);
        var challenge = challengeService.getChallengeForEdit(id);
        if (challenge == null) {
            return ResponseEntity.notFound().build();
        }
        return buildCsvExport(challenge);
    }

    private ResponseEntity<byte[]> buildCsvExport(
            final com.quiz.darkhold.challenge.entity.Challenge challenge) {
        var csv = buildCsvContent(challenge);
        var filename = challenge.getTitle().replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv")).body(csv.getBytes(StandardCharsets.UTF_8));
    }

    private String buildCsvContent(final com.quiz.darkhold.challenge.entity.Challenge challenge) {
        var header = "Question,Answer1,Answer2,Answer3,Answer4,CorrectOptions,"
                + "Type,TimeLimit,Points,AcceptableAnswers,ImageUrl,VideoUrl\n";
        var sb = new StringBuilder(header);
        if (challenge.getQuestionSets() != null) {
            challenge.getQuestionSets().forEach(q -> appendCsvRow(sb, q));
        }
        return sb.toString();
    }

    private void appendCsvRow(final StringBuilder sb,
            final com.quiz.darkhold.challenge.entity.QuestionSet question) {
        sb.append(escapeCsv(question.getQuestion())).append(",");
        sb.append(escapeCsv(question.getAnswer1())).append(",");
        sb.append(escapeCsv(question.getAnswer2())).append(",");
        sb.append(escapeCsv(question.getAnswer3())).append(",");
        sb.append(escapeCsv(question.getAnswer4())).append(",");
        sb.append(escapeCsv(question.getCorrectOptions())).append(",");
        appendExtendedFields(sb, question);
    }

    private void appendExtendedFields(final StringBuilder sb,
            final com.quiz.darkhold.challenge.entity.QuestionSet question) {
        var qType = question.getQuestionType() != null ? question.getQuestionType().name() : "";
        sb.append(escapeCsv(qType)).append(",");
        sb.append(question.getTimeLimit() != null ? question.getTimeLimit() : "").append(",");
        sb.append(question.getPoints() != null ? question.getPoints() : "").append(",");
        sb.append(escapeCsv(question.getAcceptableAnswers())).append(",");
        sb.append(escapeCsv(question.getImageUrl())).append(",");
        sb.append(escapeCsv(question.getVideoUrl())).append("\n");
    }

    private String escapeCsv(final String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Import a challenge from JSON file.
     */
    @PostMapping("/import_challenge/json")
    public ResponseEntity<ChallengeWithResponse> importChallengeFromJson(
            @RequestParam("file") final MultipartFile file) {
        logger.info("Importing challenge from JSON");
        try {
            var mapper = new ObjectMapper();
            var dto = mapper.readValue(file.getInputStream(), ChallengeExportDto.class);
            var challenge = challengeService.importFromJson(dto);
            return ResponseEntity.ok(new ChallengeWithResponse(challenge.getId(),
                    "Successfully imported " + challenge.getTitle() + " !!!"));
        } catch (java.io.IOException ioException) {
            logger.error("Error importing challenge: {}", ioException.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ChallengeWithResponse(0L, "Failed to import: " + ioException.getMessage()));
        }
    }
}
