package com.quiz.darkhold.preview.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PreviewController {
    private final Log log = LogFactory.getLog(PreviewController.class);

    @PostMapping("/preconfigure")
    public String preconfigure() {
        log.info("into the preconfigure method");
        return "preview";
    }
}
