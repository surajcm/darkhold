package com.quiz.darkhold.init;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Internationalization (i18n) configuration for the Darkhold application.
 * Configures message sources, locale resolution, and locale change interceptor.
 */
@Configuration
public class I18nConfig {

    /**
     * Configures the MessageSource for loading internationalized messages.
     * Uses ReloadableResourceBundleMessageSource for development flexibility.
     *
     * @return configured MessageSource bean
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:messages", "classpath:validation");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for 1 hour in production
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * Configures the LocaleResolver to determine the current locale.
     * Uses session-based locale storage with English as the default.
     *
     * @return configured LocaleResolver bean
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    /**
     * Configures the LocaleChangeInterceptor to allow changing locale via URL parameter.
     * Users can switch language by adding ?lang=es (or other locale code) to any URL.
     *
     * @return configured LocaleChangeInterceptor bean
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
}
