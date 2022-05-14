package com.example.quiz.adapter.in.web.edit;

import com.example.quiz.adapter.out.web.initialChoiceCount.ChoiceCountConfig;
import com.example.quiz.application.QuestionService;
import com.example.quiz.application.QuizCreator;
import com.example.quiz.domain.Question;
import com.example.quiz.domain.QuestionId;
import com.example.quiz.domain.QuizId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class QuizEditController {

    private final QuestionService questionService;
    private final QuizCreator quizCreator;
    private ChoiceCountConfig choiceCountConfig;

    public QuizEditController(QuestionService questionService, QuizCreator quizCreator, ChoiceCountConfig choiceCountConfig) {
        this.questionService = questionService;
        this.quizCreator = quizCreator;
        this.choiceCountConfig = choiceCountConfig;
    }

    @PostMapping("/add-question")
    public String addQuestion(@Valid AddQuestionForm addQuestionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-question";
        }
        try {
            questionService.add(addQuestionForm);
        } catch (NoCorrectChoiceSelected | TooManyCorrectChoicesSelected e) {
            ObjectError error = new ObjectError("Error", e.getMessage());
            bindingResult.addError(error);
            if (bindingResult.hasErrors()) {
                return "add-question";
            }
        }

        return "redirect:/add-question";
    }

    @GetMapping("/add-question")
    public String showAddQuestion(Model model) {
        String totalCount = (String) model.asMap().get("totalCount");
        AddQuestionForm addQuestionForm;
        if (!StringUtils.isEmpty(totalCount)) {
            addQuestionForm = new AddQuestionForm(Integer.parseInt(totalCount));
        }else {
            addQuestionForm = new AddQuestionForm(choiceCountConfig.getBaseNumberOfChoices());
        }

        model.addAttribute("addQuestionForm", addQuestionForm);
        return "add-question";
    }

    @PostMapping("/add-choice")
    public String addChoice(@RequestParam("index") int index, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("totalCount", index + 1);
        return "redirect:/add-question";
    }

    @PostMapping("/delete-choice")
    public String deleteChoice(@RequestParam("index") int index, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("totalCount", index - 1);
        return "redirect:/add-question";
    }

    @GetMapping("/view-questions")
    public String viewQuestions(Model model) {
        final List<Question> questions = questionService.findAll();

        final List<QuestionView> questionViews = questions.stream().map(QuestionView::of).toList();

        model.addAttribute("questions", questionViews);
        return "view-questions";
    }

    @PostMapping("/create-quiz")
    public String createQuiz(Model model) {
        QuestionId questionId = QuestionId.of(45L);
        List<QuestionId> questionIds = new ArrayList<>();
        questionIds.add(questionId);
        QuizId quizId = quizCreator.createQuiz("Quiz name", questionIds);

        return "redirect:/quiz?quizId=" + quizId.id();
    }

    @GetMapping("/quiz")
    public String quiz(Model model, @RequestParam(value = "quizId", defaultValue = "") String quizId) {
        return "quiz";
    }

    @GetMapping("/maker")
    public String maker(Model model) {
        List<Question> allQuestions = questionService.findAll();
        model.addAttribute("questions", allQuestions);
        return "maker";
    }

}