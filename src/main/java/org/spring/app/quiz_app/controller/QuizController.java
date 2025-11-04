package org.spring.app.quiz_app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.spring.app.quiz_app.model.QuizQuestion;
import org.spring.app.quiz_app.model.QuizResult;
import org.spring.app.quiz_app.model.User;
import org.spring.app.quiz_app.repo.QuizQuestionRepository;
import org.spring.app.quiz_app.repo.QuizResultRepository;
import org.spring.app.quiz_app.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class QuizController {

    @Autowired
    private QuizQuestionRepository questionRepository;
    @Autowired
    private QuizResultRepository resultRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/quiz/start")
    public String startQuiz(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) return "redirect:/";
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        if (user.isTakenQuiz()) return "redirect:/result";
        List<QuizQuestion> questions = questionRepository.findAll();
        if (questions.isEmpty()) {
            model.addAttribute("error", "No questions found. Please contact admin.");
            return "error";
        }
        // Save question order in session to prevent tampering
        List<String> qids = new ArrayList<>();
        for (QuizQuestion q : questions) qids.add(q.getId());
        session.setAttribute("activeQuiz", true);
        session.setAttribute("quizQids", qids);
        model.addAttribute("questions", questions);
        return "quiz";
    }

    @PostMapping("/quiz/submit")
    public String submitQuiz(@RequestParam MultiValueMap<String, String> form,
                             HttpServletRequest request,
                             Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) return "redirect:/";
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        Object active = session.getAttribute("activeQuiz");
        if (!(active instanceof Boolean) || !((Boolean) active)) {
            return "redirect:/result";
        }
        @SuppressWarnings("unchecked")
        List<String> qids = (List<String>) session.getAttribute("quizQids");
        if (qids == null) qids = Collections.emptyList();

        int correct = 0;
        Map<String, Integer> answers = new HashMap<>();
        for (String qid : qids) {
            Optional<QuizQuestion> qOpt = questionRepository.findById(qid);
            if (qOpt.isEmpty()) continue;
            QuizQuestion q = qOpt.get();
            String key = "q_" + qid;
            int selectedIndex = -1;
            try {
                String val = form.getFirst(key);
                if (val != null) selectedIndex = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {}
            answers.put(qid, selectedIndex);
            if (selectedIndex == q.getCorrectIndex()) correct++;
        }
        // detect if submission was auto-triggered by SBM
        String autoReason = form.getFirst("autoReason");
        boolean auto = autoReason != null && !autoReason.isBlank();
        QuizResult result = new QuizResult(user.getId(), qids.size(), correct, answers);
        result.setAutoSubmitted(auto);
        if (auto) result.setAutoReason(autoReason);
        resultRepository.save(result);
        // update user takenQuiz
        user.setTakenQuiz(true);
        userRepository.save(user);
        session.setAttribute("user", user); // refresh in session
        session.removeAttribute("activeQuiz");
        session.removeAttribute("quizQids");
        model.addAttribute("result", result);
        return "result";
    }

    @GetMapping("/result")
    public String showResult(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) return "redirect:/";
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        List<QuizResult> results = resultRepository.findByUserId(user.getId());
        if (results.isEmpty()) {
            model.addAttribute("message", "No result yet.");
            return "result";
        }
        results.sort(Comparator.comparing(QuizResult::getSubmittedAt).reversed());
        model.addAttribute("result", results.get(0));
        return "result";
    }
}
