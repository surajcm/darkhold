package com.quiz.darkhold.challenge.controller;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.service.ChallengeService;
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
    String handleFileUpload(final MultipartFile upload, final String title,
                            final String description,
                            final RedirectAttributes redirectAttributes) {
        String responseText;
        logger.info("Received incoming traffic and redirected to upload_pdf");
        logger.info(String.format("title : %s, description : %s ", title, description));
        logger.info(String.format("File details getOriginalFilename : %s, getSize : %s ",
                upload.getOriginalFilename(), upload.getSize()));
        try {
            challengeService.readProcessAndSaveChallenge(upload, title, description);
            responseText = "Successfully created " + title + " !!!";
        } catch (ChallengeException challengeException) {
            logger.error(challengeException.getMessage());
            //todo: change the http status code and give this as an error message
            responseText = "Unable to process, huge file";
        }
        return responseText;
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
        Boolean responseText = Boolean.FALSE;
        logger.info("received incoming request to delete_challenge : " + challenge);
        try {
            responseText = challengeService.deleteChallenge(challenge);
        } catch (ChallengeException challengeException) {
            logger.error(challengeException.getMessage());
        }
        return responseText;
    }
}
