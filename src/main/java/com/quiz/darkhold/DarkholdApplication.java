package com.quiz.darkhold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class DarkholdApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DarkholdApplication.class, args);
    }

}
