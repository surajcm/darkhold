package com.quiz.darkhold.challenge.controller;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.service.ChallengeService;
import com.quiz.darkhold.util.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChallengeController {
    private final Logger logger = LogManager.getLogger(ChallengeController.class);
    private final ChallengeService challengeService;

    public ChallengeController(final ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping("/options")
    public String options() {
        logger.info("on post to options");
        return "options";
    }

    @GetMapping("/options")
    public String optionsGet() {
        logger.info("on get to options");
        return "options";
    }

    /**
     * Upload the challenge as a predefined excel sheet.
     *
     * @param upload             excel file
     * @param title              game title
     * @param description        description
     * @param redirectAttributes no idea
     * @return its ajax so return a json
     */
    @PostMapping("/upload_challenge")
    public @ResponseBody
    String handleFileUpload(final MultipartFile upload,
                            final String title,
                            final String description,
                            final RedirectAttributes redirectAttributes) {
        String responseText;
        logParams(upload, title, description);
        try {
            challengeService.readProcessAndSaveChallenge(upload, title, description);
            responseText = "Successfully created " + CommonUtils.sanitizedString(title) + " !!!";
        } catch (ChallengeException challengeException) {
            logger.error(challengeException.getMessage());
            //todo: change the http status code and give this as an error message
            responseText = "Unable to process, huge file";
        }
        return responseText;
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
     * Upload the challenge as a predefined excel sheet.
     *
     * @param challenge          challenge
     * @param redirectAttributes no idea
     * @return its ajax so return a json
     */
    @DeleteMapping("/delete_challenge")
    public @ResponseBody
    Boolean deleteChallenge(final Long challenge,
                            final RedirectAttributes redirectAttributes) {
        var sanitizeChallenge = CommonUtils.sanitizedString(String.valueOf(challenge));
        logger.info("received incoming request to delete_challenge : {}",
                sanitizeChallenge);
        return challengeService.deleteChallenge(challenge);
    }
}
