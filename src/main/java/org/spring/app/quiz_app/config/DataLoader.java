package org.spring.app.quiz_app.config;

import org.spring.app.quiz_app.model.QuizQuestion;
import org.spring.app.quiz_app.model.User;
import org.spring.app.quiz_app.repo.QuizQuestionRepository;
import org.spring.app.quiz_app.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository, QuizQuestionRepository questionRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                userRepository.save(new User("admin@example.com", "admin123", "ADMIN"));
            }
            if (userRepository.findByEmail("user@example.com").isEmpty()) {
                userRepository.save(new User("user@example.com", "user123", "USER"));
            }
            if (questionRepository.count() == 0) {
                List<QuizQuestion> qs = List.of(
                        new QuizQuestion(
                                "What does JVM stand for?",
                                Arrays.asList("Java Variable Machine", "Java Virtual Machine", "Just Virtual Machine", "Joint Vendor Machine"),
                                1
                        ),
                        new QuizQuestion(
                                "Which collection does not allow duplicates?",
                                Arrays.asList("List", "Map", "Set", "Queue"),
                                2
                        ),
                        new QuizQuestion(
                                "Choose the correct HTTP method for fetching data:",
                                Arrays.asList("POST", "PUT", "GET", "DELETE"),
                                2
                        )
                );
                questionRepository.saveAll(qs);
            }
        };
    }
}
