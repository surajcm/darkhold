package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.exception.ImageValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class ImageProcessingService {

    private final Logger logger = LogManager.getLogger(ImageProcessingService.class);

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    @Value("${darkhold.media.question-images-dir:question-images}")
    private String questionImagesDir;

    @Value("${darkhold.media.max-image-width:1200}")
    private int maxImageWidth;

    @Value("${darkhold.media.max-image-height:800}")
    private int maxImageHeight;

    /**
     * Validate an image file for type, size, and format.
     *
     * @param file the image file to validate
     * @throws ImageValidationException if validation fails
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public void validateImage(final MultipartFile file) throws ImageValidationException {
        if (file == null || file.isEmpty()) {
            throw new ImageValidationException("No file provided");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageValidationException(
                    String.format("File size exceeds maximum allowed size of %d MB",
                            MAX_FILE_SIZE / (1024 * 1024)));
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ImageValidationException(
                    "Invalid file type. Allowed types: JPEG, PNG, GIF");
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidExtension(originalFilename)) {
            throw new ImageValidationException(
                    "Invalid file extension. Allowed extensions: jpg, jpeg, png, gif");
        }

        // Verify it's actually an image by trying to read it
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new ImageValidationException("File is not a valid image");
            }
        } catch (IOException ioException) {
            throw new ImageValidationException("Failed to read image file: " + ioException.getMessage());
        }
    }

    /**
     * Resize and compress an image if it exceeds max dimensions.
     *
     * @param file the original image file
     * @return byte array of the processed image
     * @throws ImageValidationException if processing fails
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public byte[] resizeAndCompressImage(final MultipartFile file) throws ImageValidationException {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (originalImage == null) {
                throw new ImageValidationException("Failed to read image");
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // Check if resizing is needed
            if (originalWidth <= maxImageWidth && originalHeight <= maxImageHeight) {
                // No resizing needed, return original
                return file.getBytes();
            }

            // Calculate new dimensions while maintaining aspect ratio
            double widthRatio = (double) maxImageWidth / originalWidth;
            double heightRatio = (double) maxImageHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);

            logger.info("Resizing image from {}x{} to {}x{}",
                    originalWidth, originalHeight, newWidth, newHeight);

            // Create resized image
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();

            // Set rendering hints for quality
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw scaled image
            graphics.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH),
                    0, 0, newWidth, newHeight, null);
            graphics.dispose();

            // Compress to JPEG with 70% quality
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);

            return outputStream.toByteArray();
        } catch (IOException ioException) {
            throw new ImageValidationException("Failed to process image: " + ioException.getMessage());
        }
    }

    /**
     * Save a question image to the filesystem.
     *
     * @param challengeId the challenge ID
     * @param questionId  the question ID
     * @param file        the image file
     * @return the relative URL path to the saved image
     * @throws ImageValidationException if save fails
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public String saveQuestionImage(final Long challengeId, final Long questionId,
                                    final MultipartFile file) throws ImageValidationException {
        validateImage(file);
        byte[] processedImageBytes = resizeAndCompressImage(file);

        // Create directory structure: question-images/{challengeId}/{questionId}/
        Path uploadPath = Paths.get(questionImagesDir, challengeId.toString(), questionId.toString());
        try {
            Files.createDirectories(uploadPath);

            // Generate filename with timestamp to prevent caching issues
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = String.format("image_%d.%s", System.currentTimeMillis(), extension);
            Path filePath = uploadPath.resolve(filename);

            // Save processed image
            Files.write(filePath, processedImageBytes);

            // Return relative URL path
            String imageUrl = String.format("/question-images/%d/%d/%s",
                    challengeId, questionId, filename);
            logger.info("Saved question image: {}", imageUrl);

            return imageUrl;
        } catch (IOException ioException) {
            throw new ImageValidationException("Failed to save image: " + ioException.getMessage());
        }
    }

    /**
     * Delete a question image from the filesystem.
     *
     * @param imageUrl the relative URL of the image to delete
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public void deleteQuestionImage(final String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Convert URL path to filesystem path
            // e.g., /question-images/1/2/image_123.jpg -> question-images/1/2/image_123.jpg
            String relativePath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            Path filePath = Paths.get(relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Deleted question image: {}", imageUrl);

                // Try to delete empty parent directories (question folder)
                Path questionDir = filePath.getParent();
                if (questionDir != null && Files.isDirectory(questionDir)
                        && isDirectoryEmpty(questionDir)) {
                    Files.delete(questionDir);
                    logger.info("Deleted empty question directory: {}", questionDir);
                }
            }
        } catch (IOException ioException) {
            logger.error("Failed to delete image {}: {}", imageUrl, ioException.getMessage());
        }
    }

    /**
     * Delete all images for a challenge.
     *
     * @param challengeId the challenge ID
     */
    public void deleteChallengeImages(final Long challengeId) {
        Path challengePath = Paths.get(questionImagesDir, challengeId.toString());
        try {
            if (Files.exists(challengePath)) {
                deleteDirectory(challengePath);
                logger.info("Deleted all images for challenge: {}", challengeId);
            }
        } catch (IOException ioException) {
            logger.error("Failed to delete challenge images for {}: {}",
                    challengeId, ioException.getMessage());
        }
    }

    private boolean hasValidExtension(final String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT));
    }

    private String getFileExtension(final String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    private boolean isDirectoryEmpty(final Path directory) throws IOException {
        try (var stream = Files.list(directory)) {
            return stream.findAny().isEmpty();
        }
    }

    private void deleteDirectory(final Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }

        try (var stream = Files.walk(directory)) {
            stream.sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ioException) {
                            logger.error("Failed to delete {}: {}", path, ioException.getMessage());
                        }
                    });
        }
    }
}
