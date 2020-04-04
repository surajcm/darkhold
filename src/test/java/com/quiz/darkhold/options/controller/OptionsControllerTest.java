package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.CommonConfigurations;
import com.quiz.darkhold.options.service.OptionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(OptionsController.class)
@ContextConfiguration(classes = {CommonConfigurations.class})
public class OptionsControllerTest {
    private MockMvc mvc;

    @Autowired
    private OptionsController optionsController;

    @MockBean
    private OptionsService optionsService;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(optionsController).build();
    }

    @Test
    public void testCreateChallenge() throws Exception {
        mvc.perform(post("/createChallenge"))
                .andExpect(status().is(200));
    }

    @Test
    public void testViewChallenge() throws Exception {
        mvc.perform(post("/viewChallenge"))
                .andExpect(status().is(200));
    }

    @Test
    public void testActiveChallenge() throws Exception {
        mvc.perform(post("/activeChallenge"))
                .andExpect(status().is(200));
    }
}