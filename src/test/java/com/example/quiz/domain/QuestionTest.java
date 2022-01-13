package com.example.quiz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QuestionTest {
    private ResponseStatus status = ResponseStatus.PENDING;

    @Test
    void knows_OneAnswer() {
        // Given
        Question question = new Question("Question 1",
                new SingleChoice(
                        new Choice("Answer 1"),
                        Collections.singletonList(
                                new Choice("Answer 1")
                        )));

        // When
        List<Choice> choices = question.choices();

        // Then
        assertThat(choices).containsExactly(new Choice("Answer 1"));
    }

    @Test
    void knows_SeveralAnswers() {
        List<Choice> choices = List.of(new Choice("Answer 1"),
                new Choice("Answer 2"),
                new Choice("Answer 3"),
                new Choice("Answer 4"));
        // Given
        Question question = new Question(
                "Question 1",
                new SingleChoice(
                        new Choice("Answer 1"),
                    choices)
        );

        // When
        List<Choice> result = question.choices();

        // Then
        assertThat(result)
                .containsExactly(
                        new Choice("Answer 1"),
                        new Choice("Answer 2"),
                        new Choice("Answer 3"),
                        new Choice("Answer 4")
                );
    }

    @Test
    void knows_correctAnswer() {
        // Given
        final Choice correct = new Choice("Answer 3");
        Question question = new Question(
                "Question 1",
                new SingleChoice(
                    correct,
                    Collections.emptyList()
                ));

        // When
        final boolean correctAnswer = question.isCorrectAnswer(correct);

        // Then
        assertThat(correctAnswer)
            .isTrue();
    }
}
