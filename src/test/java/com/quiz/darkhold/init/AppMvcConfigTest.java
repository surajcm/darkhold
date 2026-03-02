package com.quiz.darkhold.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AppMvcConfig Tests")
class AppMvcConfigTest {

    private AppMvcConfig config;
    private AppMvcInterceptor mvcInterceptor;
    private LocaleChangeInterceptor localeInterceptor;

    @BeforeEach
    void setUp() throws Exception {
        config = new AppMvcConfig();
        mvcInterceptor = new AppMvcInterceptor();
        localeInterceptor = new LocaleChangeInterceptor();
        setField("appMvcInterceptor", mvcInterceptor);
        setField("localeChangeInterceptor", localeInterceptor);
    }

    private void setField(final String name, final Object value) throws Exception {
        var field = AppMvcConfig.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(config, value);
    }

    @Test
    @DisplayName("Should register AppMvcInterceptor")
    void shouldRegisterAppMvcInterceptor() {
        var registry = mock(InterceptorRegistry.class);
        var registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(any())).thenReturn(registration);
        config.addInterceptors(registry);
        verify(registry).addInterceptor(mvcInterceptor);
    }

    @Test
    @DisplayName("Should register LocaleChangeInterceptor")
    void shouldRegisterLocaleChangeInterceptor() {
        var registry = mock(InterceptorRegistry.class);
        var registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(any())).thenReturn(registration);
        config.addInterceptors(registry);
        verify(registry).addInterceptor(localeInterceptor);
    }

    @Test
    @DisplayName("Should register user-photos resource handler")
    void shouldRegisterUserPhotosHandler() {
        var registry = mock(ResourceHandlerRegistry.class);
        var registration = mock(ResourceHandlerRegistration.class);
        when(registry.addResourceHandler(eq("/user-photos/**"))).thenReturn(registration);
        when(registry.addResourceHandler(eq("/question-images/**"))).thenReturn(registration);
        when(registration.addResourceLocations(any(String.class))).thenReturn(registration);
        config.addResourceHandlers(registry);
        verify(registry).addResourceHandler("/user-photos/**");
    }

    @Test
    @DisplayName("Should register question-images resource handler")
    void shouldRegisterQuestionImagesHandler() {
        var registry = mock(ResourceHandlerRegistry.class);
        var registration = mock(ResourceHandlerRegistration.class);
        when(registry.addResourceHandler(eq("/user-photos/**"))).thenReturn(registration);
        when(registry.addResourceHandler(eq("/question-images/**"))).thenReturn(registration);
        when(registration.addResourceLocations(any(String.class))).thenReturn(registration);
        config.addResourceHandlers(registry);
        verify(registry).addResourceHandler("/question-images/**");
    }
}
