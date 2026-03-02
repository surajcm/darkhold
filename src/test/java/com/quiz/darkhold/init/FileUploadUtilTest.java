package com.quiz.darkhold.init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("FileUploadUtil Tests")
class FileUploadUtilTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should create directory if missing")
    void shouldCreateDirectoryIfMissing() throws IOException {
        var newDir = tempDir.resolve("newdir").toString();
        var file = createMockFile("test.txt", "content");
        FileUploadUtil.saveFile(newDir, "test.txt", file);
        assertTrue(Files.exists(Path.of(newDir)));
    }

    @Test
    @DisplayName("Should save file content correctly")
    void shouldSaveFileContentCorrectly() throws IOException {
        var file = createMockFile("test.txt", "hello world");
        FileUploadUtil.saveFile(tempDir.toString(), "test.txt", file);
        var saved = Files.readString(tempDir.resolve("test.txt"));
        assertEquals("hello world", saved);
    }

    @Test
    @DisplayName("Should overwrite existing file")
    void shouldOverwriteExistingFile() throws IOException {
        var original = createMockFile("test.txt", "original");
        FileUploadUtil.saveFile(tempDir.toString(), "test.txt", original);
        var updated = createMockFile("test.txt", "updated");
        FileUploadUtil.saveFile(tempDir.toString(), "test.txt", updated);
        var content = Files.readString(tempDir.resolve("test.txt"));
        assertEquals("updated", content);
    }

    @Test
    @DisplayName("Should save to existing directory")
    void shouldSaveToExistingDirectory() throws IOException {
        var file = createMockFile("data.csv", "a,b,c");
        FileUploadUtil.saveFile(tempDir.toString(), "data.csv", file);
        assertTrue(Files.exists(tempDir.resolve("data.csv")));
    }

    @Test
    @DisplayName("Should wrap IOException with context")
    void shouldWrapIoExceptionWithContext() throws IOException {
        var badFile = mock(MultipartFile.class);
        when(badFile.getInputStream()).thenThrow(new IOException("stream error"));
        var ex = assertThrows(IOException.class,
                () -> FileUploadUtil.saveFile(tempDir.toString(), "bad.txt", badFile));
        assertTrue(ex.getMessage().contains("bad.txt"));
    }

    @Test
    @DisplayName("Should delete files in directory")
    void shouldDeleteFilesInDirectory() throws IOException {
        Files.writeString(tempDir.resolve("file1.txt"), "data1");
        Files.writeString(tempDir.resolve("file2.txt"), "data2");
        FileUploadUtil.cleanDir(tempDir.toString());
        assertFalse(Files.exists(tempDir.resolve("file1.txt")));
        assertFalse(Files.exists(tempDir.resolve("file2.txt")));
    }

    @Test
    @DisplayName("Should not delete subdirectories")
    void shouldNotDeleteSubdirectories() throws IOException {
        var subDir = tempDir.resolve("subdir");
        Files.createDirectory(subDir);
        Files.writeString(tempDir.resolve("file.txt"), "data");
        FileUploadUtil.cleanDir(tempDir.toString());
        assertTrue(Files.exists(subDir));
        assertFalse(Files.exists(tempDir.resolve("file.txt")));
    }

    @Test
    @DisplayName("Should handle empty directory in cleanDir")
    void shouldHandleEmptyDirectoryInCleanDir() {
        assertDoesNotThrow(
                () -> FileUploadUtil.cleanDir(tempDir.toString()));
    }

    @Test
    @DisplayName("Should handle non-existent directory in cleanDir")
    void shouldHandleNonExistentDirInCleanDir() {
        var noDir = tempDir.resolve("nonexistent").toString();
        assertDoesNotThrow(() -> FileUploadUtil.cleanDir(noDir));
    }

    private MockMultipartFile createMockFile(final String name, final String content) {
        return new MockMultipartFile(
                "file", name, "text/plain", content.getBytes());
    }
}
