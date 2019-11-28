package com.quiz.darkhold.challenge.controller;

import com.quiz.darkhold.challenge.service.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChallengeController {
    @Autowired
    private ChallengeService challengeService;

    private final Logger logger = LoggerFactory.getLogger(ChallengeController.class);

    @PostMapping("/options")
    public String options() {
        logger.info("on to options");
        return "options";
    }

    @PostMapping("/upload_challenge")
    public @ResponseBody
    String handleFileUpload(MultipartFile upload, String title, String description,
                            RedirectAttributes redirectAttributes) {
        String responseText;
        logger.info("received incoming traffic and redirected to upload_pdf");
        logger.info("title : " + title);
        logger.info("description : " + description);
        logger.info("File details getOriginalFilename : " + upload.getOriginalFilename());
        logger.info("File details getSize : " + upload.getSize());
        try {
            challengeService.readProcessAndSaveChallenge(upload,title,description);
            responseText = "Successfully created " + title + " !!!";
        } catch (Exception e) {
            logger.error(e.getMessage());
            //todo: change the http status code and give this as an error message
            responseText = "Unable to process, huge file";
        }
        return responseText;
    }
}
