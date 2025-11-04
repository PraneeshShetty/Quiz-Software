package org.spring.app.quiz_app.repo;

import org.spring.app.quiz_app.model.QuizResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizResultRepository extends MongoRepository<QuizResult, String> {
    List<QuizResult> findByUserId(String userId);
    QuizResult findTopByUserIdOrderBySubmittedAtDesc(String userId);
    List<QuizResult> findByAutoSubmittedTrueOrderBySubmittedAtDesc();
}
