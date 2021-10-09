package com.example.quiz;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuizTest {
    private QuestionStatus status = QuestionStatus.PENDING;

    @Test
    void new_quiz_hasNoQuestions() {
        // Given / when
        Quiz quiz = new Quiz();

        // Then
        List<Question> questions = quiz.questions();

        assertThat(questions)
                .isEmpty();
    }

    @Test
    void new_quiz_hasOneQuestion() {
        // Given / when
        List<Answer> choices = List.of(new Answer("Answer 1"), new Answer("Answer 2"));

        Quiz quiz = new Quiz(new Question(
                "Question 1",
                new MultipleChoice(
                        new Answer("Answer 2"),
                        choices
                ), status
        ));

        List<Question> questions = quiz.questions();

        assertThat(questions).containsOnly(
                new Question("Question 1",
                        new MultipleChoice(
                                new Answer("Answer 2"),
                                List.of(
                                        new Answer("Answer 1"),
                                        new Answer("Answer 2")
                                )), status));
    }

    @Test
    void new_quiz_hasManyQuestions() {
        // Given / when
        Quiz quiz = new Quiz(
                new Question(
                        "Question 1",
                        new MultipleChoice(
                                new Answer("Answer 1"),
                                Collections.emptyList()),
                        status
                ),
                new Question("Question 2",
                        new MultipleChoice(
                                new Answer("Answer 2"),
                                Collections.emptyList())
                        , status
                ));

        List<Question> questions = quiz.questions();

        assertThat(questions).containsOnly(
                new Question("Question 1",
                        new MultipleChoice(
                                new Answer("Answer 1"),
                                Collections.emptyList()),
                        status
                ),
                new Question("Question 2",
                        new MultipleChoice(
                                new Answer("Answer 2"),
                                Collections.emptyList()
                        ), status
                )
        );
    }

    @Test
    void knows_its_grade_for_allPending() {
        // Given / when
        Quiz quiz = new Quiz(
                new Question(
                        "Question 1",
                        new MultipleChoice(
                                new Answer("Answer 1"),
                                Collections.emptyList()),
                        status
                ),
                new Question("Question 2",
                        new MultipleChoice(
                                new Answer("Answer 2"),
                                Collections.emptyList())
                        , status
                ),
                new Question("Question 3",
                        new MultipleChoice(
                                new Answer("Answer 1"),
                                Collections.emptyList())
                        , status
                )

        );

        assertThat(quiz.grade()).isEqualTo(new Grade(3, new FinalMark(3, 0, 0)));
    }

    @Test
    void knows_grade_forAllCorrect() {
        // Given / when
        Quiz quiz = new Quiz(
                new Question(
                        "Question 1",
                        new MultipleChoice(
                                new Answer("Answer 1"),
                                Collections.emptyList()),
                        status
                ),
                new Question("Question 2",
                        new MultipleChoice(
                                new Answer("Answer 1"),
                                Collections.emptyList())
                        , status
                ),
                new Question("Question 3",
                        new MultipleChoice(
                                new Answer("Answer 1"),
                                Collections.emptyList())
                        , status
                )

        );

        // when
        quiz.questions().forEach((q) -> {
            quiz.mark(q, new Answer("Answer 1"));
        });

        assertThat(quiz.grade()).isEqualTo(new Grade(3, new FinalMark(0, 0, 3)));
    }
}
