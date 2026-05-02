package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ValidFileType annotation;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        context = mock(ConstraintValidatorContext.class);
        annotation = mock(ValidFileType.class);
        
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/png", "image/jpeg"});
        when(annotation.message()).thenReturn("Invalid file type");
        
        validator.initialize(annotation);
    }

    @Test
    void isValid_WhenFileIsNull_ReturnsFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void isValid_WhenContentTypeIsNull_ReturnsFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.png", null, new byte[0]);
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_WhenTypeNotAllowed_ReturnsFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);
        assertFalse(validator.isValid(file, context));
    }

    // Note: Testing the ImageIO.read(file.getInputStream()) part is tricky because it depends on real image data.
    // I'll try to provide a minimal valid PNG.
    @Test
    void isValid_WhenValidImage_ReturnsTrue() throws IOException {
        // Minimal 1x1 black PNG
        byte[] validPng = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
            (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x08, (byte) 0xD7, 0x63, 0x60, 0x00, 0x02,
            0x00, 0x00, 0x05, 0x00, 0x01, 0x0D, 0x26, (byte) 0xE5, 0x2E, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validPng);
        assertTrue(validator.isValid(file, context));
    }

    @Test
    void isValid_WhenInvalidImageData_ReturnsFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "not an image".getBytes());
        assertFalse(validator.isValid(file, context));
    }
}
