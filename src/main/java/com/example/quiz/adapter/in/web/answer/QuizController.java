package com.example.quiz.adapter.in.web.answer;

import com.example.quiz.hexagon.application.QuizSessionService;
import com.example.quiz.hexagon.application.port.QuestionRepository;
import com.example.quiz.hexagon.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class QuizController {

    private final QuizSessionService quizSessionService;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuizController(QuizSessionService quizSessionService, QuestionRepository questionRepository) {
        this.quizSessionService = quizSessionService;
        this.questionRepository = questionRepository;
    }

    @GetMapping("/")
    public String home() {
        return "start";
    }

    @GetMapping("/question")
    public String askQuestion(Model model, @RequestParam(value = "token", defaultValue = "") String token) {
        if (token.isBlank()) {
            return "redirect:/";
        }

        if (quizSessionService.isFinished(token)) {
            return "redirect:/result?token=" + token;
        }

        // refactor - feature envy - cohere method
        // 1. Extract method
        // 2. Introduce parameter - for target of move
        // 3. Move method - F6
        QuestionId questionId = quizSessionService.findSessionByToken(token).currentQuestionId();
        Question question = questionRepository.findById(questionId).orElseThrow();
        AskQuestionForm askQuestionForm = AskQuestionForm.from(question);
        model.addAttribute("askQuestionForm", askQuestionForm);
        model.addAttribute("token", token);
        return templateFor(question);
    }

    @PostMapping("/question")
    public String questionResponse(AskQuestionForm askQuestionForm, @RequestParam(value = "token") String token) {
        QuizSession quizSession = quizSessionService.findSessionByToken(token);
        // Could get question - from quizSession.currentQuestionId()
        // use questionId in respondWith()
        QuestionId questionId = quizSession.currentQuestionId();
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);
        Quiz quiz = quizSessionService.findQuizById(quizSession.quizId());

        // Why have to pass quiz to respondWith? - quizSession already knows about the quiz
        quizSession.respondWith(question, quiz, askQuestionForm.getSelectedChoices());
        if (quizSession.isFinished(quiz)) {
            return "redirect:/result?token=" + token;
        }
        return "redirect:/question?token=" + token;
    }

    @GetMapping("/result")
    public String showResult(Model model, @RequestParam(value = "token", defaultValue = "") String token) {
        if (token.isBlank()) {
            return "redirect:/";
        }

        Grade grade = quizSessionService.findSessionByToken(token).grade();
        model.addAttribute("resultView", ResultView.from(grade, questionRepository));
        return "result";
    }

    // Add QuizId
    @PostMapping("/start")
    public String start(@RequestParam(value = "quizId", defaultValue = "") long id) {
//        String token = tokenGenerator.token();
//        // use QuizId in startSession
//        quizSessionService.startSessionWithToken(token);
        String token = quizSessionService.createQuizSession(QuizId.of(id));
        return "redirect:/question?token=" + token;
    }

    private String templateFor(Question question) {
        if (question.isSingleChoice()) {
            return "single-choice";
        } else {
            return "multiple-choice";
        }
    }
}