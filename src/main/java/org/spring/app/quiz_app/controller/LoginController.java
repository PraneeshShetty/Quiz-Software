package org.spring.app.quiz_app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.spring.app.quiz_app.model.User;
import org.spring.app.quiz_app.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/do-login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpServletRequest request,
                          Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
        HttpSession session = request.getSession(true);
        User u = userOpt.get();
        session.setAttribute("user", u);
        // If admin logs in from main login, redirect to admin panel
        if ("ADMIN".equals(u.getRole())) {
            return "redirect:/admin";
        }
        // Regular user flow
        if (u.isTakenQuiz()) {
            return "redirect:/result";
        }
        return "redirect:/quiz/start";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
