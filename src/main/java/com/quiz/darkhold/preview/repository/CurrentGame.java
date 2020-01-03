package com.quiz.darkhold.preview.repository;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;

public class CurrentGame {

    public void initializeDB() {
        Nitrite db = Nitrite.builder()
                .compressed()
                .filePath("/tmp/test.db")
                .openOrCreate("user", "password");

        // Create a Nitrite Collection
        NitriteCollection collection = db.getCollection("test");
    }
    public void saveCurrentStatus() {
        Nitrite db = Nitrite.builder()
                .compressed()
                .filePath("/tmp/test.db")
                .openOrCreate("user", "password");
    }
}
