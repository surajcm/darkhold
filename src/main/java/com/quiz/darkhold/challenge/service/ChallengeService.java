package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.Options;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.challenge.repository.QuestionSetRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChallengeService {
    private final Logger logger = LogManager.getLogger(ChallengeService.class);
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private QuestionSetRepository questionSetRepository;

    /**
     * Read the excel, process it, extract data and save it to the database.
     *
     * @param upload      the excel file
     * @param title       challenge name
     * @param description challenge desc
     * @throws ChallengeException on error
     */
    public void readProcessAndSaveChallenge(final MultipartFile upload, final String title,
                                            final String description)
            throws ChallengeException {
        List<QuestionSet> questionSets = readAndProcessChallenge(upload);
        Challenge challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setQuestionSets(questionSets);
        final Challenge savedChallenge = challengeRepository.save(challenge);
        questionSets.forEach(q -> q.setChallenge(savedChallenge));
        questionSets.forEach(questionSet -> questionSetRepository.save(questionSet));
    }

    public Boolean deleteChallenge(final Long challengeId) throws ChallengeException {
        Boolean response = Boolean.FALSE;
        Optional<Challenge> challenge = challengeRepository.findById(challengeId);
        if (challenge.isPresent()) {
            logger.info("Challenge present in database");
            List<QuestionSet> questionSets = challenge.get().getQuestionSets();
            questionSetRepository.deleteAll(questionSets);
            challengeRepository.delete(challenge.get());
            response = Boolean.TRUE;
        }
        return response;
    }

    private List<QuestionSet> readAndProcessChallenge(final MultipartFile upload) throws ChallengeException {
        List<QuestionSet> questionSets = new LinkedList<>();
        try (Workbook workbook = new XSSFWorkbook(upload.getInputStream());) {
            Sheet datatypeSheet = workbook.getSheetAt(0);
            for (Row currentRow : datatypeSheet) {
                questionSets.add(populateQuestionSet(currentRow));
            }
        } catch (IOException | NotOfficeXmlFileException exception) {
            logger.error(exception.getMessage());
            throw new ChallengeException("Unable to process the file..");
        }
        return questionSets;
    }

    private QuestionSet populateQuestionSet(final Row currentRow) {
        Iterator<Cell> cellIterator = currentRow.iterator();
        QuestionSet questionSet = new QuestionSet();
        while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            switch (currentCell.getColumnIndex()) {
                case 0:
                    questionSet.setQuestion(currentCell.getStringCellValue());
                    break;
                case 1:
                    questionSet.setAnswer1(fetchCurrentCellValue(currentCell));
                    break;
                case 2:
                    questionSet.setAnswer2(fetchCurrentCellValue(currentCell));
                    break;
                case 3:
                    questionSet.setAnswer3(fetchCurrentCellValue(currentCell));
                    break;
                case 4:
                    questionSet.setAnswer4(fetchCurrentCellValue(currentCell));
                    break;
                case 5:
                    String options = currentCell.getStringCellValue();
                    String optionsList = populateOptionsFromString(options);
                    questionSet.setCorrectOptions(optionsList);
                    break;
                default:
                    logger.error(String.format("Unknown option at %s , Text is : %s",
                            currentCell.getColumnIndex(), currentCell.getStringCellValue()));
            }
        }
        logger.info("Current questions : " + questionSet);
        return questionSet;
    }

    private String fetchCurrentCellValue(final Cell currentCell) {
        String result = null;
        if (currentCell.getCellType() == CellType.STRING) {
            result = currentCell.getStringCellValue();
        } else if (currentCell.getCellType() == CellType.NUMERIC) {
            result = String.valueOf(currentCell.getNumericCellValue());
        } else if (currentCell.getCellType() == CellType.BOOLEAN) {
            result = String.valueOf(currentCell.getBooleanCellValue());
        } else {
            logger.error("Invalid cell type on  " + currentCell.getColumnIndex()
                    + " is " + currentCell.getCellType().name());
        }
        return result;
    }

    private String populateOptionsFromString(final String options) {
        if (!options.contains(",")) {
            return Options.valueOf(options).name();
        } else {
            Stream<String> option = Arrays.stream(options.split(","));
            return option.map(Options::valueOf).map(Enum::name).collect(Collectors.joining(","));
        }
    }

}
