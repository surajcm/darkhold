package com.quiz.darkhold.challenge.controller;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.service.ChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DisplayName("ChallengeController Tests")
class ChallengeControllerTest {

    @Mock
    private ChallengeService challengeService;

    @Mock
    private RedirectAttributes redirectAttributes;

    private ChallengeController challengeController;

    @BeforeEach
    void setUp() {
        challengeController = new ChallengeController(challengeService);
    }

    @Nested
    @DisplayName("Options endpoint tests")
    class OptionsEndpointTests {

        @Test
        @DisplayName("Should handle POST /options and return view name")
        void shouldHandlePostOptions() {
            String result = challengeController.options();
            assertEquals("options/options", result);
        }

        @Test
        @DisplayName("Should handle GET /options and return view name")
        void shouldHandleGetOptions() {
            String result = challengeController.optionsGet();
            assertEquals("options/options", result);
        }
    }

    @Nested
    @DisplayName("File upload endpoint tests")
    class FileUploadEndpointTests {

        private MultipartFile mockFile;
        private String testTitle;
        private String testDescription;

        @BeforeEach
        void setUp() {
            testTitle = "Test Challenge";
            testDescription = "Test Description";
            mockFile = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "test data".getBytes(UTF_8));
        }

        @Test
        @DisplayName("Should successfully upload challenge and return success response")
        void shouldSuccessfullyUploadChallenge() throws ChallengeException {
            Long expectedChallengeId = 123L;
            when(challengeService.readProcessAndSaveChallenge(mockFile, testTitle, testDescription))
                    .thenReturn(expectedChallengeId);

            ResponseEntity<ChallengeController.ChallengeWithResponse> responseEntity =
                    challengeController.handleFileUpload(mockFile, testTitle, testDescription, redirectAttributes);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            var response = responseEntity.getBody();
            assertNotNull(response);
            assertEquals(expectedChallengeId, response.challengeId());
            assertTrue(response.message().contains("Successfully created"));
            assertTrue(response.message().contains(testTitle));
            verify(challengeService).readProcessAndSaveChallenge(mockFile, testTitle, testDescription);
        }

        @Test
        @DisplayName("Should handle ChallengeException during file upload")
        void shouldHandleChallengeException() throws ChallengeException {
            when(challengeService.readProcessAndSaveChallenge(any(MultipartFile.class), anyString(), anyString()))
                    .thenThrow(new ChallengeException("File processing error"));

            ResponseEntity<ChallengeController.ChallengeWithResponse> responseEntity =
                    challengeController.handleFileUpload(mockFile, testTitle, testDescription, redirectAttributes);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            var response = responseEntity.getBody();
            assertNotNull(response);
            assertEquals(0L, response.challengeId());
            assertEquals("File processing error", response.message());
            verify(challengeService).readProcessAndSaveChallenge(mockFile, testTitle, testDescription);
        }

        @Test
        @DisplayName("Should handle file with special characters in title")
        void shouldHandleSpecialCharactersInTitle() throws ChallengeException {
            String titleWithSpecialChars = "Test <Challenge> & Co.";
            Long expectedChallengeId = 456L;
            when(challengeService.readProcessAndSaveChallenge(mockFile, titleWithSpecialChars, testDescription))
                    .thenReturn(expectedChallengeId);

            var responseEntity = challengeController.handleFileUpload(mockFile,
                    titleWithSpecialChars, testDescription, redirectAttributes);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
            assertEquals(expectedChallengeId, responseEntity.getBody().challengeId());
        }

        @Test
        @DisplayName("Should handle empty title")
        void shouldHandleEmptyTitle() throws ChallengeException {
            String emptyTitle = "";
            Long expectedChallengeId = 789L;
            when(challengeService.readProcessAndSaveChallenge(mockFile, emptyTitle, testDescription))
                    .thenReturn(expectedChallengeId);

            ResponseEntity<ChallengeController.ChallengeWithResponse> responseEntity =
                    challengeController.handleFileUpload(mockFile, emptyTitle, testDescription, redirectAttributes);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            var response = responseEntity.getBody();
            assertNotNull(response);
            assertEquals(expectedChallengeId, response.challengeId());
            verify(challengeService).readProcessAndSaveChallenge(mockFile, emptyTitle, testDescription);
        }

        @Test
        @DisplayName("Should handle large file size")
        void shouldHandleLargeFileSize() throws ChallengeException {
            byte[] largeData = new byte[10 * 1024 * 1024];
            MultipartFile largeFile = new MockMultipartFile("file", "large.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", largeData);
            when(challengeService.readProcessAndSaveChallenge(largeFile, testTitle, testDescription))
                    .thenThrow(new ChallengeException("File too large"));

            var responseEntity = challengeController.handleFileUpload(
                    largeFile, testTitle, testDescription, redirectAttributes);

            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertEquals(0L, responseEntity.getBody().challengeId());
        }


        @Test
        @DisplayName("Should handle null title")
        void shouldHandleNullTitle() throws ChallengeException {
            when(challengeService.readProcessAndSaveChallenge(mockFile, null, testDescription))
                    .thenReturn(111L);

            ResponseEntity<ChallengeController.ChallengeWithResponse> responseEntity =
                    challengeController.handleFileUpload(mockFile, null, testDescription, redirectAttributes);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            var response = responseEntity.getBody();
            assertNotNull(response);
            assertEquals(111L, response.challengeId());
        }

        @Test
        @DisplayName("Should handle null description")
        void shouldHandleNullDescription() throws ChallengeException {
            when(challengeService.readProcessAndSaveChallenge(mockFile, testTitle, null))
                    .thenReturn(222L);

            ResponseEntity<ChallengeController.ChallengeWithResponse> responseEntity =
                    challengeController.handleFileUpload(mockFile, testTitle, null, redirectAttributes);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            var response = responseEntity.getBody();
            assertNotNull(response);
            assertEquals(222L, response.challengeId());
        }
    }

    @Nested
    @DisplayName("Delete challenge endpoint tests")
    class DeleteChallengeEndpointTests {

        @Test
        @DisplayName("Should successfully delete challenge")
        void shouldSuccessfullyDeleteChallenge() {
            Long challengeId = 123L;
            when(challengeService.deleteChallenge(challengeId)).thenReturn(true);

            var responseEntity = challengeController.deleteChallenge(challengeId, redirectAttributes);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertTrue(responseEntity.getBody());
            verify(challengeService).deleteChallenge(challengeId);
        }

        @Test
        @DisplayName("Should return 404 when challenge deletion fails")
        void shouldReturn404WhenDeletionFails() {
            Long challengeId = 456L;
            when(challengeService.deleteChallenge(challengeId)).thenReturn(false);

            var responseEntity = challengeController.deleteChallenge(challengeId, redirectAttributes);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            verify(challengeService).deleteChallenge(challengeId);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent challenge")
        void shouldHandleDeletionOfNonExistentChallenge() {
            Long nonExistentChallengeId = 999999L;
            when(challengeService.deleteChallenge(nonExistentChallengeId)).thenReturn(false);

            var responseEntity = challengeController.deleteChallenge(
                    nonExistentChallengeId, redirectAttributes);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        }

        @Test
        @DisplayName("Should handle delete with zero challenge ID")
        void shouldHandleDeleteWithZeroChallengeId() {
            Long zeroChallengeId = 0L;
            when(challengeService.deleteChallenge(zeroChallengeId)).thenReturn(false);

            var responseEntity = challengeController.deleteChallenge(
                    zeroChallengeId, redirectAttributes);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        }

        @Test
        @DisplayName("Should handle delete with negative challenge ID")
        void shouldHandleDeleteWithNegativeChallengeId() {
            Long negativeChallengeId = -1L;
            when(challengeService.deleteChallenge(negativeChallengeId)).thenReturn(false);

            var responseEntity = challengeController.deleteChallenge(
                    negativeChallengeId, redirectAttributes);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        }

        @Test
        @DisplayName("Should handle delete with large challenge ID")
        void shouldHandleDeleteWithLargeChallengeId() {
            Long largeChallengeId = Long.MAX_VALUE;
            when(challengeService.deleteChallenge(largeChallengeId)).thenReturn(false);

            var responseEntity = challengeController.deleteChallenge(
                    largeChallengeId, redirectAttributes);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        }
    }

    @Nested
    @DisplayName("ChallengeWithResponse record tests")
    class ChallengeWithResponseTests {

        @Test
        @DisplayName("Should create ChallengeWithResponse with valid values")
        void shouldCreateChallengeWithResponse() {
            Long challengeId = 123L;
            String message = "Success";

            ChallengeController.ChallengeWithResponse response =
                    new ChallengeController.ChallengeWithResponse(challengeId, message);

            assertNotNull(response);
            assertEquals(challengeId, response.challengeId());
            assertEquals(message, response.message());
        }

        @Test
        @DisplayName("Should create ChallengeWithResponse with null message")
        void shouldCreateChallengeWithResponseNullMessage() {
            Long challengeId = 456L;
            String message = null;

            ChallengeController.ChallengeWithResponse response =
                    new ChallengeController.ChallengeWithResponse(challengeId, message);

            assertNotNull(response);
            assertEquals(challengeId, response.challengeId());
            assertNull(response.message());
        }

        @Test
        @DisplayName("Should create ChallengeWithResponse with empty message")
        void shouldCreateChallengeWithResponseEmptyMessage() {
            Long challengeId = 789L;
            String message = "";

            ChallengeController.ChallengeWithResponse response =
                    new ChallengeController.ChallengeWithResponse(challengeId, message);

            assertNotNull(response);
            assertEquals(challengeId, response.challengeId());
            assertEquals(message, response.message());
        }

        @Test
        @DisplayName("Should create ChallengeWithResponse with zero challenge ID")
        void shouldCreateChallengeWithResponseZeroChallengeId() {
            Long challengeId = 0L;
            String message = "Failed";

            ChallengeController.ChallengeWithResponse response =
                    new ChallengeController.ChallengeWithResponse(challengeId, message);

            assertNotNull(response);
            assertEquals(challengeId, response.challengeId());
            assertEquals(message, response.message());
        }
    }

    @Nested
    @DisplayName("Controller instantiation tests")
    class ControllerInstantiationTests {

        @Test
        @DisplayName("Should instantiate controller with valid service")
        void shouldInstantiateControllerWithValidService() {
            ChallengeService service = mock(ChallengeService.class);
            ChallengeController controller = new ChallengeController(service);

            assertNotNull(controller);
        }

        @Test
        @DisplayName("Should instantiate controller with null service")
        void shouldInstantiateControllerWithNullService() {
            ChallengeController controller = new ChallengeController(null);

            assertNotNull(controller);
        }
    }
}
