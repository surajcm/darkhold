package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.options.OptionsConfigurations;
import com.quiz.darkhold.options.service.OptionsService;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.service.PreviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(OptionsController.class)
@ContextConfiguration(classes = {OptionsConfigurations.class})
class OptionsControllerTest {
    private MockMvc mvc;

    @Autowired
    private OptionsController optionsController;

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private PreviewService previewService;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(optionsController).build();
    }

    @Test
    void testCreateChallenge() throws Exception {
        mvc.perform(post("/createChallenge"))
                .andExpect(status().is(200));
    }

    @Test
    void testViewChallenge() throws Exception {
        mvc.perform(post("/viewChallenge"))
                .andExpect(status().is(200));
    }

    @Test
    void testActiveChallenge() throws Exception {
        mvc.perform(post("/activeChallenge"))
                .andExpect(status().is(200));
    }

    @Test
    void validActiveChallenge() throws Exception {
        when(previewService.getActiveChallenge()).thenReturn(new PublishInfo());
        mvc.perform(post("/activeChallenge"))
                .andExpect(status().is(200));
    }
}