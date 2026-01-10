package com.quiz.darkhold.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class AppMvcConfig implements WebMvcConfigurer {
    @Autowired
    AppMvcInterceptor appMvcInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(appMvcInterceptor);
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // User photos
        var dirName = "user-photos";
        var userPhotosDirName = Paths.get("user-photos");
        var userPhotosPath = userPhotosDirName.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:" + userPhotosPath + "/");

        // Question images
        var questionImagesDirName = Paths.get("question-images");
        var questionImagesPath = questionImagesDirName.toFile().getAbsolutePath();
        registry.addResourceHandler("/question-images/**")
                .addResourceLocations("file:" + questionImagesPath + "/");
    }
}
