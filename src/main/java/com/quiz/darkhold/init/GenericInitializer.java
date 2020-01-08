package com.quiz.darkhold.init;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericInitializer {

    @Bean
    public NitriteCollection createNitriteCollection() {
        Nitrite db = Nitrite.builder()
                .compressed()
                .filePath("/tmp/test.db")
                .openOrCreate("user", "password");
        // Create a Nitrite Collection
        NitriteCollection collection = db.getCollection("test");
        return collection;
    }

}
