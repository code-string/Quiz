package com.example.quiz.domain;

import com.example.quiz.domain.factories.QuizTestFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QuizSessionTest {
    @Test
    void emptyQuizThrowsException() {
        // Given
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(0);

        assertThatThrownBy(quiz::start)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sessionStartsWithTheFirstQuestion() {
        // Given
        final ChoiceType choice = new SingleChoice(Collections.singletonList(new Choice("Answer 1", true)));

        final Question question = new Question("Question 1", choice);
        List<Question> questions = List.of(question);
        Quiz quiz = new Quiz(questions);

        // When
        QuizSession session = quiz.start();

        assertThat(session.question())
                .isEqualTo(question);
    }

    @Test
//  testTakerCanCheckIfSessionWithOneQuestionIsFinished
    void givenQuizWithOneQuestionWhenQuestionIsAnsweredSessionIsFinished() {
        // Given
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(1);
        QuizSession session = quiz.start();
        Question question = session.question();

        // when
        session.respondWith(new Choice("Answer 1"));

        // Then
        assertThat(session.isFinished())
                .isTrue();
    }

    @Test
        // testTakerCanCheckIfSessionIsFinishedWithThreeQuestionsAfterSecondQuestion
    void quizWithThreeQuestionsWhenAnsweringTwoQuestionsSessionIsNotFinished() {

        // Given
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(3);
        QuizSession session = quiz.start();

        // when
        Question q1 = session.question();
        session.respondWith(new Choice("Answer 1"));
        Question q2 = session.question();
        session.respondWith(new Choice("Answer 2"));

        assertThat(session.isFinished())
                .isFalse();
    }

    @Test
    void testTakerCanCheckIfSessionWithThreeQuestionsIsFinishedAfterThirdQuestion() {
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(3);
        QuizSession session = quiz.start();

        // when
        Question q1 = session.question();
        session.respondWith(new Choice("Answer 1"));
        Question q2 = session.question();
        session.respondWith(new Choice("Answer 2"));
        Question q3 = session.question();
        session.respondWith(new Choice("Answer 2"));

        assertThat(session.isFinished())
                .isTrue();
    }

    // Ask Grade

    @Test
    void grade_gives_number_of_correct_responses_for_Session() {
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(3);
        QuizSession session = quiz.start();

        // when
        Question q1 = session.question();
        session.respondWith(new Choice("Answer 1", true));
        Question q2 = session.question();
        session.respondWith(new Choice("Answer 2", false));
        Question q3 = session.question();
        session.respondWith(new Choice("Answer 2", false));

        assertThat(session.correctResponsesCount())
                .isEqualTo(1L);
    }

    @Test
    void counts_incorrect_responses() {
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(3);
        QuizSession session = quiz.start();

        // when
        Question q1 = session.question();
        session.respondWith(new Choice("Answer 1", true));
        Question q2 = session.question();
        session.respondWith(new Choice("Answer 2", false));
        Question q3 = session.question();
        session.respondWith(new Choice("Answer 2", false));

        assertThat(session.incorrectResponsesCount())
                .isEqualTo(2L);
    }

    @Test
    void calculatesGradeForFinishedTest() {
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(3);
        QuizSession session = quiz.start();

        // when
        Question q1 = session.question();
        Choice choice1 = new Choice("Answer 1", true);
        session.respondWith(choice1);
        Question q2 = session.question();
        Choice choice2 = new Choice("Answer 2", false);
        session.respondWith(choice2);
        Question q3 = session.question();
        Choice choice3 = new Choice("Answer 2", false);
        session.respondWith(choice3);

        List<Response> responses = List.of(
                new Response(q1.getId(), q1.isCorrectAnswer(choice1), choice1),
                new Response(q2.getId(), q2.isCorrectAnswer(choice2), choice2),
                new Response(q3.getId(), q3.isCorrectAnswer(choice3), choice3));

        final Grade grade = new Grade(responses, 1, 2);

        final Grade result = session.grade();
        assertThat(result)
                .isEqualTo(grade);
    }

    @Test
    void returnSameQuestionIfItHasntBeenAnswered() {
        // Given
        Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(1);
        QuizSession session = quiz.start();

        // When
        Question q1 = session.question();
        Question q2 = session.question();

        // Then
        assertThat(q1)
                .isEqualTo(q2);
    }

    @Test
    void quizWithTwoQuestionsWhenResponseToFirstQuestionThenSecondQuestionIsCurrent() {
        final Quiz quiz = QuizTestFactory.createQuizWithSingleChoiceQuestions(2);

        final QuizSession session = quiz.start();

        final Question q1 = session.question();
        session.respondWith(new Choice("text"));

        final Question q2 = session.question();

        assertThat(q1)
                .isNotEqualTo(q2);
    }
}
