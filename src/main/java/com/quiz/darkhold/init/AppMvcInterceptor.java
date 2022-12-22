package com.quiz.darkhold.init;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class AppMvcInterceptor implements HandlerInterceptor {
    public AppMvcInterceptor() {
        MDC.put("transactionId", UUID.randomUUID().toString());
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        var traceID = UUID.randomUUID().toString();
        MDC.put("transactionId", traceID);
        return true;
    }
}
