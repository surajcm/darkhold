package com.quiz.darkhold.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.file.Paths;

@Configuration
public class AppMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AppMvcInterceptor appMvcInterceptor;

    @Autowired
    private LocaleChangeInterceptor localeChangeInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(appMvcInterceptor);
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // User photos
        String dirName = "user-photos";
        java.nio.file.Path userPhotosDirName = Paths.get("user-photos");
        String userPhotosPath = userPhotosDirName.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:" + userPhotosPath + "/");

        // Question images
        java.nio.file.Path questionImagesDirName = Paths.get("question-images");
        String questionImagesPath = questionImagesDirName.toFile().getAbsolutePath();
        registry.addResourceHandler("/question-images/**")
                .addResourceLocations("file:" + questionImagesPath + "/");
    }
}
