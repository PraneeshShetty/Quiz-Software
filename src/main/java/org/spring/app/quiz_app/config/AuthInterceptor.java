package org.spring.app.quiz_app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.spring.app.quiz_app.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // allow static resources
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/")) {
            return true;
        }
        // Public endpoints (user and admin login)
        if (uri.equals("/") || uri.equals("/login") || uri.equals("/do-login")
                || uri.equals("/admin/login") || uri.equals("/admin/do-login")) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/");
            return false;
        }
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            response.sendRedirect("/");
            return false;
        }
        User user = (User) userObj;
        // One attempt enforcement: block access to /quiz if already taken unless submitting result view only
        if (uri.startsWith("/quiz") && user.isTakenQuiz()) {
            response.sendRedirect("/result");
            return false;
        }
        return true;
    }
}
