package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.dto.ChallengeExportDto;
import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.Options;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;
import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.challenge.repository.QuestionSetRepository;
import com.quiz.darkhold.user.entity.DarkholdUserDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChallengeService {
    private final Logger logger = LogManager.getLogger(ChallengeService.class);
    private final ChallengeRepository challengeRepository;
    private final QuestionSetRepository questionSetRepository;

    public ChallengeService(final ChallengeRepository challengeRepository,
                            final QuestionSetRepository questionSetRepository) {
        this.challengeRepository = challengeRepository;
        this.questionSetRepository = questionSetRepository;
    }

    /**
     * Read the Excel, process it, extract data and save it to the database.
     *
     * @param upload      the Excel file
     * @param title       challenge name
     * @param description challenge desc
     * @throws ChallengeException on error
     */
    public Long readProcessAndSaveChallenge(final MultipartFile upload,
                                            final String title,
                                            final String description)
            throws ChallengeException {
        var questionSets = readAndProcessChallenge(upload);
        var challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setQuestionSets(questionSets.stream().toList());
        challenge.setChallengeOwner(currentUserId());
        final var savedChallenge = challengeRepository.save(challenge);
        questionSets.forEach(q -> q.setChallenge(savedChallenge));
        questionSetRepository.saveAll(questionSets);
        return savedChallenge.getId();
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

    /**
     * Create an empty challenge (without questions from Excel).
     *
     * @param title       challenge title
     * @param description challenge description
     * @return the created challenge
     */
    public Challenge createEmptyChallenge(final String title, final String description) {
        var challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setChallengeOwner(currentUserId());
        return challengeRepository.save(challenge);
    }

    /**
     * Update an existing challenge's title and description.
     *
     * @param challengeId the challenge ID
     * @param title       new title
     * @param description new description
     * @return the updated challenge or null if not found/not owned
     */
    public Challenge updateChallenge(final Long challengeId, final String title, final String description) {
        var challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            var challenge = challengeOpt.get();
            if (!challenge.getChallengeOwner().equals(currentUserId())) {
                logger.warn("User {} attempted to update challenge {} owned by {}",
                        currentUserId(), challengeId, challenge.getChallengeOwner());
                return null;
            }
            challenge.setTitle(title);
            challenge.setDescription(description);
            return challengeRepository.save(challenge);
        }
        return null;
    }

    /**
     * Duplicate a challenge with all its questions.
     *
     * @param challengeId the challenge to duplicate
     * @return the new challenge or null if not found/not owned
     */
    public Challenge duplicateChallenge(final Long challengeId) {
        var challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isEmpty()) {
            return null;
        }
        var original = challengeOpt.get();
        if (!original.getChallengeOwner().equals(currentUserId())) {
            logger.warn("User {} attempted to duplicate challenge {} owned by {}",
                    currentUserId(), challengeId, original.getChallengeOwner());
            return null;
        }

        // Create new challenge with copied title
        var newChallenge = new Challenge();
        newChallenge.setTitle(original.getTitle() + " (Copy)");
        newChallenge.setDescription(original.getDescription());
        newChallenge.setChallengeOwner(currentUserId());
        var savedChallenge = challengeRepository.save(newChallenge);

        // Duplicate all questions
        if (original.getQuestionSets() != null) {
            var newQuestions = new ArrayDeque<QuestionSet>();
            int order = 0;
            for (var origQ : original.getQuestionSets()) {
                var newQ = copyQuestionFields(origQ);
                newQ.setDisplayOrder(order++);
                newQ.setChallenge(savedChallenge);
                newQuestions.add(newQ);
            }
            questionSetRepository.saveAll(newQuestions);
        }

        return savedChallenge;
    }

    /**
     * Get a challenge by ID if owned by current user.
     *
     * @param challengeId the challenge ID
     * @return the challenge or null if not found/not owned
     */
    public Challenge getChallengeForEdit(final Long challengeId) {
        var challengeOpt = challengeRepository.findById(challengeId);
        if (challengeOpt.isPresent()) {
            var challenge = challengeOpt.get();
            if (challenge.getChallengeOwner().equals(currentUserId())) {
                return challenge;
            }
            logger.warn("User {} attempted to access challenge {} owned by {}",
                    currentUserId(), challengeId, challenge.getChallengeOwner());
        }
        return null;
    }

    /**
     * Import a challenge from JSON DTO.
     */
    public Challenge importFromJson(final ChallengeExportDto dto) {
        var challenge = new Challenge();
        challenge.setTitle(dto.getTitle());
        challenge.setDescription(dto.getDescription());
        challenge.setChallengeOwner(currentUserId());
        var savedChallenge = challengeRepository.save(challenge);

        if (dto.getQuestions() != null) {
            var questions = new ArrayDeque<QuestionSet>();
            int order = 0;
            for (var qDto : dto.getQuestions()) {
                var qs = copyQuestionFieldsFromDto(qDto);
                qs.setDisplayOrder(order++);
                qs.setChallenge(savedChallenge);
                questions.add(qs);
            }
            questionSetRepository.saveAll(questions);
        }
        return savedChallenge;
    }

    private QuestionSet copyQuestionFields(final QuestionSet src) {
        var dest = new QuestionSet();
        dest.setQuestion(src.getQuestion());
        dest.setAnswer1(src.getAnswer1());
        dest.setAnswer2(src.getAnswer2());
        dest.setAnswer3(src.getAnswer3());
        dest.setAnswer4(src.getAnswer4());
        dest.setCorrectOptions(src.getCorrectOptions());
        dest.setQuestionType(src.getQuestionType());
        dest.setTimeLimit(src.getTimeLimit());
        dest.setPoints(src.getPoints());
        dest.setAcceptableAnswers(src.getAcceptableAnswers());
        return dest;
    }

    private QuestionSet copyQuestionFieldsFromDto(final ChallengeExportDto.QuestionExportDto src) {
        var dest = new QuestionSet();
        dest.setQuestion(src.getQuestion());
        dest.setAnswer1(src.getAnswer1());
        dest.setAnswer2(src.getAnswer2());
        dest.setAnswer3(src.getAnswer3());
        dest.setAnswer4(src.getAnswer4());
        dest.setCorrectOptions(src.getCorrectOptions());
        dest.setQuestionType(src.getQuestionType());
        dest.setTimeLimit(src.getTimeLimit());
        dest.setPoints(src.getPoints());
        dest.setAcceptableAnswers(src.getAcceptableAnswers());
        return dest;
    }

    private Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal();
        return ((DarkholdUserDetails) principal).getUser().getId();
    }

    //replace LinkedList with ArrayDeque as the return type
    private ArrayDeque<QuestionSet> readAndProcessChallenge(final MultipartFile upload) throws ChallengeException {
        ArrayDeque<QuestionSet> questionSets = new ArrayDeque<>();
        try (var workbook = new XSSFWorkbook(upload.getInputStream());) {
            var datatypeSheet = workbook.getSheetAt(0);
            for (Row currentRow : datatypeSheet) {
                questionSets.add(populateQuestionSet(currentRow));
            }
        } catch (IOException | NotOfficeXmlFileException exception) {
            logger.error(exception);
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
                case 5 -> questionSet.setCorrectOptions(parseCorrectOptions(currentCell));
                case 6 -> questionSet.setQuestionType(parseQuestionType(currentCell));
                case 7 -> questionSet.setTimeLimit(parseTimeLimit(currentCell));
                case 8 -> questionSet.setPoints(parsePoints(currentCell));
                case 9 -> questionSet.setAcceptableAnswers(fetchCurrentCellValue(currentCell));
                case 10 -> questionSet.setImageUrl(fetchCurrentCellValue(currentCell));
                case 11 -> questionSet.setVideoUrl(fetchCurrentCellValue(currentCell));
                default -> logger.debug("Extra column at {}", currentCell.getColumnIndex());
            }
        }
        logger.info("Current questions : {}", questionSet);
        return questionSet;
    }

    private String parseCorrectOptions(final Cell cell) {
        var options = fetchCurrentCellValue(cell);
        if (options == null || options.isBlank()) {
            return null;
        }
        return populateOptionsFromString(options.trim());
    }

    private QuestionType parseQuestionType(final Cell cell) {
        var value = fetchCurrentCellValue(cell);
        if (value == null || value.isBlank()) {
            return QuestionType.MULTIPLE_CHOICE;
        }
        try {
            return QuestionType.valueOf(value.trim().toUpperCase(Locale.ROOT).replace(" ", "_"));
        } catch (IllegalArgumentException ex) {
            logger.warn("Unknown question type: {}, defaulting to MULTIPLE_CHOICE", value);
            return QuestionType.MULTIPLE_CHOICE;
        }
    }

    private Integer parseTimeLimit(final Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        var value = fetchCurrentCellValue(cell);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            logger.warn("Invalid time limit: {}", value);
            return null;
        }
    }

    private Integer parsePoints(final Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        var value = fetchCurrentCellValue(cell);
        if (value == null || value.isBlank()) {
            return 1000; // Default points
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            logger.warn("Invalid points: {}, defaulting to 1000", value);
            return 1000;
        }
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
            var cellName = currentCell.getCellType().name();
            logger.error("Invalid cell type on  {}  is {}", currentCell.getColumnIndex(),
                    cellName);
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
