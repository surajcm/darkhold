package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.preview.PreviewConfigurations;
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
import org.springframework.ui.ConcurrentModel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PreviewController.class)
@ContextConfiguration(classes = {PreviewConfigurations.class})
public class PreviewControllerTest {
    private MockMvc mvc;

    @Autowired
    private PreviewController previewController;

    @Autowired
    private PreviewService previewService;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(previewController).build();
    }

    @Test
    public void verifyPreconfigure() throws Exception {
        mvc.perform(post("/preconfigure"))
                .andExpect(status().is(400));
    }

    @Test
    public void verifyPreconfigureWithValues() throws Exception {
        mvc.perform(post("/preconfigure", new ConcurrentModel(), "challenge"))
                .andExpect(status().is(400));
    }

}