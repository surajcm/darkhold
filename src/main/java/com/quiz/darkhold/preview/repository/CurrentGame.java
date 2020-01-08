package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.preview.model.PublishInfo;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.dizitart.no2.filters.Filters.eq;

@Repository
public class CurrentGame {

    private final Logger logger = LoggerFactory.getLogger(CurrentGame.class);

    @Autowired
    private NitriteCollection collection;

    public void saveCurrentStatus(PublishInfo publishInfo) {
        List<String> users = new ArrayList<>();
        users.add(publishInfo.getUsername());
        // create a document to populate data
        Document doc = Document.createDocument("pin", publishInfo.getPin())
                .put("users", users);

        // insert the document
        collection.insert(doc);
    }

    public List<String> getActiveUsersInGame(String pin) {
        Cursor cursor = collection.find(Filters.and(eq("pin", pin)));
        List<String> users = (List<String>) cursor.toList().get(0).get("users");
        logger.info("Participants are :" + users);
        return users;
    }
}
