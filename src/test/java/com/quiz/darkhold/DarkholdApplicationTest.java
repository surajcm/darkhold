package com.quiz.darkhold;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("DarkholdApplication Tests")
class DarkholdApplicationTest {

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads(final ApplicationContext applicationContext) {
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    @DisplayName("Application class should be instantiable")
    void applicationClassCanBeInstantiated() {
        assertDoesNotThrow(DarkholdApplication::new,
                "DarkholdApplication should be instantiable without throwing exceptions");
    }

    @Test
    @DisplayName("Main method should not throw exceptions")
    void mainMethodExecutesWithoutException() {
        assertDoesNotThrow(() -> DarkholdApplication.main(new String[]{}),
                "Main method should execute without throwing exceptions");
    }

    @Test
    @DisplayName("Spring Boot application bean should be present")
    void springBootApplicationBeanExists(final ApplicationContext applicationContext) {
        assertNotNull(applicationContext.getBean(DarkholdApplication.class),
                "DarkholdApplication bean should be registered in the application context");
    }

}

