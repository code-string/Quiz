package com.example.quiz.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.function.Predicate.not;

public class QuizSession {

    private final Iterator<Question> iterator;
    private final List<Question> questions;
    private SessionId id;

    private final List<Response> responses = new ArrayList<>();
    private Question question;
    private String token;


    public void setId(SessionId id) {
        this.id = id;
    }

    public SessionId getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    // for testing purposes

    QuizSession() {
        this.iterator = null;
        this.questions = null;
    }

    public QuizSession(Quiz quiz) {
        questions = quiz.questions();
        if (questions.isEmpty()) {
            throw new IllegalArgumentException();
        }
        iterator = questions.iterator();
        question = iterator.next();
    }

    public Question question() {
        return question;
    }

    public void respondWith(int... indices) {
        Choice[] selectedChoices = Arrays.stream(indices)
                                         .mapToObj(index -> question.choices().get(index))
                                         .toArray(Choice[]::new);
        respondWith(selectedChoices);
    }

    public void respondWith(Choice... choices) {
        Response response = new Response(question, choices);
        responses.add(response);
        if (iterator.hasNext()) {
            question = iterator.next();
        }
    }

    public boolean isFinished() {
        return responses.size() == questions.size();
    }

    public int correctResponsesCount() {
        return Math.toIntExact(responses.stream()
                                        .filter(Response::isCorrect)
                                        .count());
    }

    public int incorrectResponsesCount() {
        return Math.toIntExact(responses.stream()
                                        .filter(not(Response::isCorrect))
                                        .count());
    }

    public Grade grade() {
        return new Grade(responses, correctResponsesCount(), incorrectResponsesCount());
    }
}