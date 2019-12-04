package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.service.PreviewService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PreviewController {
    private final Log log = LogFactory.getLog(PreviewController.class);

    @Autowired
    private PreviewService previewService;

    @PostMapping("/preconfigure")
    public String preconfigure(Model model, @RequestParam("challenges") String challenges) {
        log.info("into the preconfigure method : " + challenges);
        PreviewInfo previewInfo = previewService.fetchQuestions(challenges);
        model.addAttribute("previewInfo", previewInfo);
        return "preview";
    }

    @PostMapping("/publish")
    public String publish() {
        //todo: work on this
        return "index";
    }
}
