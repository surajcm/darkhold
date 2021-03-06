package com.quiz.darkhold.challenge.controller;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.service.ChallengeService;
import com.quiz.darkhold.util.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChallengeController {
    private final Logger logger = LogManager.getLogger(ChallengeController.class);
    @Autowired
    private ChallengeService challengeService;

    @PostMapping("/options")
    public String options() {
        logger.info("on to options");
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
        logger.info("title : {}, description : {} ",
                CommonUtils.sanitizedString(title), CommonUtils.sanitizedString(description));
        logger.info("File details getOriginalFilename : {}, getSize : {}} ",
                CommonUtils.sanitizedString(upload.getOriginalFilename()),
                CommonUtils.sanitizedString(String.valueOf(upload.getSize())));
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
        logger.info("received incoming request to delete_challenge : {}",
                CommonUtils.sanitizedString(String.valueOf(challenge)));
        return challengeService.deleteChallenge(challenge);
    }
}
