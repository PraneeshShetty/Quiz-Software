package org.spring.app.quiz_app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "results")
public class QuizResult {
    @Id
    private String id;
    private String userId;
    private int totalQuestions;
    private int correct;
    private Map<String, Integer> answers; // questionId -> selectedIndex
    private Instant submittedAt = Instant.now();
    private boolean autoSubmitted = false;
    private String autoReason;

    public QuizResult() {}

    public QuizResult(String userId, int totalQuestions, int correct, Map<String, Integer> answers) {
        this.userId = userId;
        this.totalQuestions = totalQuestions;
        this.correct = correct;
        this.answers = answers;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrect() { return correct; }
    public void setCorrect(int correct) { this.correct = correct; }

    public Map<String, Integer> getAnswers() { return answers; }
    public void setAnswers(Map<String, Integer> answers) { this.answers = answers; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }

    public boolean isAutoSubmitted() { return autoSubmitted; }
    public void setAutoSubmitted(boolean autoSubmitted) { this.autoSubmitted = autoSubmitted; }

    public String getAutoReason() { return autoReason; }
    public void setAutoReason(String autoReason) { this.autoReason = autoReason; }
}
