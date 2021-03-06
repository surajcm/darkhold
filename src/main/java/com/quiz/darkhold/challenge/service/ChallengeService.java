package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.Options;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.challenge.repository.QuestionSetRepository;
import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChallengeService {
    private final Logger logger = LogManager.getLogger(ChallengeService.class);
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private QuestionSetRepository questionSetRepository;
    @Autowired
    private UserRepository userRepository;

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
        var questionSets = readAndProcessChallenge(upload);
        var challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setQuestionSets(questionSets);
        challenge.setChallengeOwner(currentUserId());
        final var savedChallenge = challengeRepository.save(challenge);
        questionSets.forEach(q -> q.setChallenge(savedChallenge));
        questionSets.forEach(questionSet -> questionSetRepository.save(questionSet));
    }

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username);
        return user.getId();
    }

    public Boolean deleteChallenge(final Long challengeId) {
        var response = Boolean.FALSE;
        var challenge = challengeRepository.findById(challengeId);
        if (challenge.isPresent()) {
            logger.info("Challenge present in database");
            var questionSets = challenge.get().getQuestionSets();
            questionSetRepository.deleteAll(questionSets);
            challengeRepository.delete(challenge.get());
            response = Boolean.TRUE;
        }
        return response;
    }

    private List<QuestionSet> readAndProcessChallenge(final MultipartFile upload) throws ChallengeException {
        List<QuestionSet> questionSets = new LinkedList<>();
        try (var workbook = new XSSFWorkbook(upload.getInputStream());) {
            var datatypeSheet = workbook.getSheetAt(0);
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
        var cellIterator = currentRow.iterator();
        var questionSet = new QuestionSet();
        while (cellIterator.hasNext()) {
            var currentCell = cellIterator.next();
            switch (currentCell.getColumnIndex()) {
                case 0 -> questionSet.setQuestion(currentCell.getStringCellValue());
                case 1 -> questionSet.setAnswer1(fetchCurrentCellValue(currentCell));
                case 2 -> questionSet.setAnswer2(fetchCurrentCellValue(currentCell));
                case 3 -> questionSet.setAnswer3(fetchCurrentCellValue(currentCell));
                case 4 -> questionSet.setAnswer4(fetchCurrentCellValue(currentCell));
                case 5 -> {
                    var options = currentCell.getStringCellValue();
                    var optionsList = populateOptionsFromString(options);
                    questionSet.setCorrectOptions(optionsList);
                }
                default -> logger.error(String.format("Unknown option at %s , Text is : %s",
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
