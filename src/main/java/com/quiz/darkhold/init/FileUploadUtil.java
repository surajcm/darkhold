package com.quiz.darkhold.init;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class FileUploadUtil {
    public static void saveFile(final String uploadDir,
                                final String fileName,
                                final MultipartFile multipartFile) throws IOException {
        final Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (final InputStream inputStream = multipartFile.getInputStream()) {
            final Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public static void cleanDir(final String dir) {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            stream.forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (final IOException ioe) {
                        System.out.println("Could not delete file: " + file);
                    }
                }
            });
        } catch (final IOException ioe) {
            System.out.println("Could not list directory: " + dir);
        }
    }
}
