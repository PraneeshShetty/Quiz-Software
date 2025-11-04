package org.spring.app.quiz_app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.spring.app.quiz_app.model.User;
import org.spring.app.quiz_app.model.QuizResult;
import org.spring.app.quiz_app.repo.UserRepository;
import org.spring.app.quiz_app.repo.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuizResultRepository quizResultRepository;

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Object u = session.getAttribute("user");
        if (!(u instanceof User)) return false;
        return "ADMIN".equals(((User) u).getRole());
    }

    // Admin login page
    @GetMapping("/admin/login")
    public String adminLoginPage(HttpServletRequest request) {
        // If already logged in as admin, go to admin home
        if (isAdmin(request)) return "redirect:/admin";
        return "admin_login";
    }

    // Admin login action
    @PostMapping("/admin/do-login")
    public String adminDoLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpServletRequest request,
                               Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password) ||
                !"ADMIN".equals(userOpt.get().getRole())) {
            model.addAttribute("error", "Invalid admin credentials");
            return "admin_login";
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("user", userOpt.get());
        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String adminHome(HttpServletRequest request, Model model) {
        if (!isAdmin(request)) return "redirect:/";
        // Fetch only auto-submitted results
        List<QuizResult> autoResults = quizResultRepository.findByAutoSubmittedTrueOrderBySubmittedAtDesc();
        // Map userId -> User for quick lookup
        Map<String, User> usersById = new HashMap<>();
        for (QuizResult r : autoResults) {
            String uid = r.getUserId();
            usersById.computeIfAbsent(uid, id -> userRepository.findById(id).orElse(null));
        }
        model.addAttribute("autoResults", autoResults);
        model.addAttribute("usersById", usersById);
        return "admin";
    }

    @PostMapping("/admin/reset/{userId}")
    public String resetAttempt(@PathVariable String userId, HttpServletRequest request) {
        if (!isAdmin(request)) return "redirect:/";
        Optional<User> u = userRepository.findById(userId);
        if (u.isPresent()) {
            User user = u.get();
            user.setTakenQuiz(false);
            userRepository.save(user);
        }
        return "redirect:/admin";
    }
}
