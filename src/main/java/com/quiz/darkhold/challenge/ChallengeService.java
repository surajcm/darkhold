package com.quiz.darkhold.challenge;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.model.Challenge;
import com.quiz.darkhold.challenge.model.Options;
import com.quiz.darkhold.challenge.model.QuestionSet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChallengeService {
    private final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

    public void readProcessAndSaveChallenge(MultipartFile upload, String title, String description)
            throws ChallengeException {
        List<QuestionSet> questionSets = readAndProcessChallenge(upload);
        Challenge challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setQuestionSets(questionSets);
        //save challenge to db
    }

    private List<QuestionSet> readAndProcessChallenge(MultipartFile upload) throws ChallengeException {
        List<QuestionSet> questionSets = new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(upload.getInputStream());
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {
                QuestionSet questionSet = new QuestionSet();
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    switch (currentCell.getColumnIndex()) {
                        case 0:
                            questionSet.setQuestion(currentCell.getStringCellValue());
                            break;
                        case 1:
                            questionSet.setAnswer1(currentCell.getStringCellValue());
                            break;
                        case 2:
                            questionSet.setAnswer2(currentCell.getStringCellValue());
                            break;
                        case 3:
                            questionSet.setAnswer3(currentCell.getStringCellValue());
                            break;
                        case 4:
                            questionSet.setAnswer4(currentCell.getStringCellValue());
                            break;
                         case 5:
                             String options = currentCell.getStringCellValue();
                             List<Options> optionsList = populateOptionsFromString(options);
                             questionSet.setCorrectOption(optionsList);
                             break;
                        default:
                            logger.error("Unknown option at "+currentCell.getColumnIndex()
                                    + ", Text is : "+ currentCell.getStringCellValue());
                    }
                }
                logger.info("Current questions : " + questionSet);
                questionSets.add(questionSet);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ChallengeException("Unable to process the file..");
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return questionSets;
    }

    private List<Options> populateOptionsFromString(String options) {
        if (!options.contains(",")) {
            return Collections.singletonList(Options.valueOf(options));
        } else {
            Stream<String> option = Arrays.stream(options.split(","));
            return option.map(Options::valueOf).collect(Collectors.toList());
        }
    }

}
