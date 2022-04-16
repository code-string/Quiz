package com.example.quiz.adapter.in.web.answer;

import com.example.quiz.adapter.in.web.QuizControllerTestFactory;
import com.example.quiz.application.QuizService;
import com.example.quiz.application.QuizSessionService;
import com.example.quiz.application.QuizSessionServiceTestFactory;
import com.example.quiz.application.port.InMemoryQuestionRepository;
import com.example.quiz.application.port.InMemoryQuizSessionRepository;
import com.example.quiz.application.port.QuestionRepository;
import com.example.quiz.domain.Question;
import com.example.quiz.domain.factories.SingleChoiceQuestionTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuizControllerTest {

    public static final QuestionRepository DUMMY_QUESTION_REPOSITORY = null;

    @Test
    void afterQuizStartedAskForQuestionReturnsFirstQuestion() {
        QuizController quizController = QuizControllerTestFactory.createAndStartQuizControllerWithOneSingleChoiceQuestion();
        final Model model = new ConcurrentModel();
        quizController.start();
        quizController.askQuestion(model, "stub-id-1");
        final AskQuestionForm askQuestion = (AskQuestionForm) model.getAttribute("askQuestionForm");

        assertThat(askQuestion.getQuestion())
                .isEqualTo("Question 1");
    }

    @Test
    void storesFormResponseAnswerInQuizSessionMarkedAsCorrectAnswer() {
        QuizSessionService quizSessionService = QuizSessionServiceTestFactory.createQuizSessionService();
        StubTokenGenerator stubIdGenerator = new StubTokenGenerator();
        QuizController quizController = new QuizController(quizSessionService, stubIdGenerator, DUMMY_QUESTION_REPOSITORY);
        quizController.start();

        final Model model = new ConcurrentModel();

        quizController.askQuestion(model, "stub-id-1");
        AskQuestionForm askQuestionForm = new AskQuestionForm();
        askQuestionForm.setSelectedChoices(0);
        quizController.questionResponse(askQuestionForm, "stub-id-1");

        assertThat(quizSessionService.findSessionByToken("stub-id-1").correctResponsesCount())
                .isEqualTo(1L);
    }

    @Test
    void afterRespondingToLastQuestionShowsResults() {
        QuizController quizController = QuizControllerTestFactory.createAndStartQuizControllerWithOneSingleChoiceQuestion();
        final Model model = new ConcurrentModel();
        quizController.start();
        quizController.askQuestion(model, "stub-id-1");
        AskQuestionForm askQuestionForm = new AskQuestionForm();
        askQuestionForm.setSelectedChoices(0);
        final String redirectPage = quizController.questionResponse(askQuestionForm, "stub-id-1");

        assertThat(redirectPage)
                .isEqualTo("redirect:/result?token=stub-id-1");
    }

    @Test
    void afterQuestionResponseResultViewHasQuestion() {
        // given
        final QuizController quizController = QuizControllerTestFactory.createAndStartQuizControllerWithOneSingleChoiceQuestion();
        final Model model = new ConcurrentModel();
        quizController.start();

        // when
        AskQuestionForm askQuestionForm = new AskQuestionForm();
        askQuestionForm.setSelectedChoices(1);
        quizController.questionResponse(askQuestionForm, "stub-id-1");
        quizController.showResult(model, "stub-id-1");

        // then
        ResultView resultView = (ResultView) model.getAttribute("resultView");
        List<ResponseView> responseViews = resultView.getResponsesViews();

        assertThat(responseViews)
                .hasSize(1);
        assertThat(responseViews.get(0).getQuestionView().getText())
                .isEqualTo("Question 1");
    }

    @Test
    void askQuestionTwiceGoesToSamePage() {
        // Given
        final QuizController quizController = QuizControllerTestFactory.createAndStartQuizControllerWithOneSingleChoiceQuestion();
        final Model model = new ConcurrentModel();
        quizController.start();
        quizController.askQuestion(model, "stub-id-1");

        // When
        final String page = quizController.askQuestion(model, "stub-id-1");

        // Then
        assertThat(page)
                .isEqualTo("single-choice");
    }

    @Test
    void askingQuestionOnAFinishedQuizReturnsResult() {
        QuizController quizController = QuizControllerTestFactory.createAndStartQuizControllerWithOneSingleChoiceQuestion();
        ConcurrentModel model = new ConcurrentModel();
        quizController.start();

        quizController.askQuestion(model, "stub-id-1");
        AskQuestionForm askQuestionForm = new AskQuestionForm();
        askQuestionForm.setSelectedChoices(0);
        quizController.questionResponse(askQuestionForm, "stub-id-1");
        String redirectPage = quizController.askQuestion(model, "stub-id-1");

        assertThat(redirectPage)
                .isEqualTo("redirect:/result?token=stub-id-1");
    }


    @Test
    void afterStartCreateSessionAndRedirectToQuiz() {
        QuizController quizController = QuizControllerTestFactory.createAndStartQuizControllerWithOneSingleChoiceQuestion();
        String redirect = quizController.start();
        assertThat(redirect)
                .isEqualTo("redirect:/quiz?token=stub-id-1");
    }

    @Test
    void multipleChoiceQuestionReturnsMultipleChoicePage() {
        QuizController quizController = QuizControllerTestFactory.createAndStartChoiceQuizControllerWithOneMultipleChoiceQuestion();
        ConcurrentModel model = new ConcurrentModel();
        quizController.start();
        String redirect = quizController.askQuestion(model, "stub-id-1");

        assertThat(redirect)
                .isEqualTo("multiple-choice");
        assertThat(model.containsAttribute("askQuestionForm"))
                .isTrue();
    }

    @Test
    void askQuestionPullsQuestionFromSessionBasedOnId() {
        Question singleChoiceQuestion = SingleChoiceQuestionTestFactory.createSingleChoiceQuestion();
        InMemoryQuestionRepository inMemoryQuestionRepository = new InMemoryQuestionRepository();
        inMemoryQuestionRepository.save(singleChoiceQuestion);
        QuizService quizService = new QuizService(inMemoryQuestionRepository);
        QuizSessionService quizSessionService = new QuizSessionService(quizService, new InMemoryQuizSessionRepository());
        StubTokenGenerator stubIdGenerator = new StubTokenGenerator();
        QuizController quizController = new QuizController(quizSessionService, stubIdGenerator, DUMMY_QUESTION_REPOSITORY);
        quizController.start();
        quizController.start();
    }
}