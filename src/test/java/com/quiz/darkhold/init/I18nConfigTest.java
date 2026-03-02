package com.quiz.darkhold.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("I18nConfig Tests")
class I18nConfigTest {

    private I18nConfig config;

    @BeforeEach
    void setUp() {
        config = new I18nConfig();
    }

    @Test
    @DisplayName("Should return ReloadableResourceBundleMessageSource")
    void shouldReturnReloadableMessageSource() {
        var messageSource = config.messageSource();
        assertInstanceOf(ReloadableResourceBundleMessageSource.class, messageSource);
    }

    @Test
    @DisplayName("Should return non-null message source")
    void shouldReturnNonNullMessageSource() {
        assertNotNull(config.messageSource());
    }

    @Test
    @DisplayName("Should use code as default message")
    void shouldUseCodeAsDefaultMessage() {
        var messageSource = config.messageSource();
        var resolved = messageSource.getMessage("nonexistent.key", null, Locale.ENGLISH);
        assertEquals("nonexistent.key", resolved);
    }

    @Test
    @DisplayName("Should return SessionLocaleResolver")
    void shouldReturnSessionLocaleResolver() {
        var resolver = config.localeResolver();
        assertInstanceOf(SessionLocaleResolver.class, resolver);
    }

    @Test
    @DisplayName("Should set default locale to English")
    void shouldSetDefaultLocaleToEnglish() {
        var resolver = config.localeResolver();
        var request = new MockHttpServletRequest();
        var locale = resolver.resolveLocale(request);
        assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    @DisplayName("Should return LocaleChangeInterceptor")
    void shouldReturnLocaleChangeInterceptor() {
        var interceptor = config.localeChangeInterceptor();
        assertInstanceOf(LocaleChangeInterceptor.class, interceptor);
    }

    @Test
    @DisplayName("Should set param name to lang")
    void shouldSetParamNameToLang() {
        var interceptor = config.localeChangeInterceptor();
        assertEquals("lang", interceptor.getParamName());
    }

    @Test
    @DisplayName("Should return non-null locale resolver")
    void shouldReturnNonNullLocaleResolver() {
        assertNotNull(config.localeResolver());
    }
}
