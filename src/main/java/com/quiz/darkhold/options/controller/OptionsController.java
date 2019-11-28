package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.options.model.ChallengeInfo;
import com.quiz.darkhold.options.model.ChallengeSummary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OptionsController {
    private final Log log = LogFactory.getLog(OptionsController.class);

    @PostMapping("/createChallenge")
    public String createChallenge() {
        log.info("into the createChallenge method");
        return "createchallenge";
    }

    @PostMapping("/viewChallenge")
    public String viewChallenges(Model model) {
        log.info("into the viewChallenge method");
        model.addAttribute("challengeInfo", mockChallengeInfo());
        return "viewchallenges";
    }

    private ChallengeInfo mockChallengeInfo() {
        ChallengeInfo challengeInfo = new ChallengeInfo();
        List<ChallengeSummary> summaries = new ArrayList<>();

        ChallengeSummary summary1 = new ChallengeSummary();
        summary1.setChallengeId(1);
        summary1.setChallengeName("AWS QUIZ 101");
        summaries.add(summary1);

        ChallengeSummary summary2= new ChallengeSummary();
        summary2.setChallengeId(2);
        summary2.setChallengeName("AWS QUIZ 102");
        summaries.add(summary2);

        ChallengeSummary summary3 = new ChallengeSummary();
        summary3.setChallengeId(3);
        summary3.setChallengeName("AWS QUIZ 103");
        summaries.add(summary3);

        ChallengeSummary summary4= new ChallengeSummary();
        summary4.setChallengeId(4);
        summary4.setChallengeName("AWS QUIZ 104");
        summaries.add(summary4);

        ChallengeSummary summary5= new ChallengeSummary();
        summary5.setChallengeId(5);
        summary5.setChallengeName("AWS QUIZ 102");
        summaries.add(summary5);

        challengeInfo.setChallengeSummaryList(summaries);
        return challengeInfo;
    }
}
