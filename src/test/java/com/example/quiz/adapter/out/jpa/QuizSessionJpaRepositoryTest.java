package com.example.quiz.adapter.out.jpa;

import com.example.quiz.domain.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuizSessionJpaRepositoryTest implements TestContainerConfiguration {

    @Autowired
    QuizSessionJpaRepository quizSessionJpaRepository;


    @Test
    void savesAndRetrievesAQuizSessionWithNoResponses() {
        // Given
        QuizSessionDbo quizSessionDbo = new QuizSessionDboBuilder()
                .withCurrentQuestionId(QuestionId.of(2L))
                .withDefaultStartedAt()
                .withToken("stub-1")
                .build();

        // When
        QuizSessionDbo savedSession = quizSessionJpaRepository.save(quizSessionDbo);

        // Then
        assertThat(savedSession.getId())
                .isNotNull();
    }

    @Test
    void saveAndRetrieveAQuizSessionWithMultipleResponses() {
        // Given
        Question question = new QuestionBuilder()
                .withDefaultSingleChoice()
                .withQuestionId(1L).build();
        Response response1 = new ResponseBuilder()
                .withQuestion(question)
                .withCorrectChoice()
                .build();
        Response response2 = new ResponseBuilder()
                .withQuestion(question)
                .withCorrectChoice()
                .build();
        QuizSessionDbo quizSessionDbo = new QuizSessionDboBuilder()
                .withCurrentQuestionId(question.getId())
                .withDefaultStartedAt()
                .withToken("stub-1")
                .withResponse(response1)
                .withResponse(response2)
                .build();

        // When
        QuizSessionDbo savedSession = quizSessionJpaRepository.save(quizSessionDbo);

        // Then
        assertThat(savedSession.getResponses().size())
                .isEqualTo(2);
    }
}