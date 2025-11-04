package org.spring.app.quiz_app.repo;

import org.spring.app.quiz_app.model.QuizQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizQuestionRepository extends MongoRepository<QuizQuestion, String> {
}
