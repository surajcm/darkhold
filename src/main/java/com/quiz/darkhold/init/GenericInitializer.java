package com.quiz.darkhold.init;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericInitializer {

    /**
     * Create the bean for in memory nitrate db.
     *
     * @return bean of nitrate
     */
    @Bean
    public NitriteCollection createNitriteCollection() {
        Nitrite db = Nitrite.builder()
                .compressed()
                .filePath("/tmp/test.db")
                .openOrCreate("user", "password");
        // Create a Nitrite Collection
        return db.getCollection("test");
    }

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }


}
