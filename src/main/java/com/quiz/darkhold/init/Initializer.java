package com.quiz.darkhold.init;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Suraj Muraleedharan
 * on 1/9/17.
 */
@Component
public class Initializer {

    /**
     * This is to run a sql manager that comes up with hsqldb, while starting the app.
     */
    @PostConstruct
    public void init() {
        System.setProperty("java.awt.headless", "false");
        DatabaseManagerSwing.main(
                new String[]{"--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", ""});
    }

}
