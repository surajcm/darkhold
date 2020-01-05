package com.quiz.darkhold.preview.repository;

import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
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
        List abc = new ArrayList<>();
        abc.add("apple");
        abc.add("orange");
        Nitrite db = Nitrite.builder()
                .compressed()
                .filePath("/tmp/test.db")
                .openOrCreate("user", "password");
        // Create a Nitrite Collection
        NitriteCollection collection = db.getCollection("test");
        // create a document to populate data
        Document doc = Document.createDocument("firstName", "John")
                .put("lastName", "Doe")
                .put("fruits", abc)
                .put("note", "a quick brown fox jump over the lazy dog");

        // insert the document
        collection.insert(doc);
    }
}
