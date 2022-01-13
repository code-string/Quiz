package com.example.quiz.adapter.in.web;

import com.example.quiz.domain.Choice;
import com.example.quiz.domain.Question;
import java.util.List;

public class AskQuestionForm {
  private String question;
  private List<String> choices;
  private String selectedChoice;

  public static AskQuestionForm from(Question question) {
    final AskQuestionForm askQuestionForm = new AskQuestionForm();
    askQuestionForm.setQuestion(question.text());
    final List<String> choices = question.choices()
        .stream()
        .map(Choice::text)
        .toList();
    askQuestionForm.setChoices(choices);

    return askQuestionForm;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public List<String> getChoices() {
    return choices;
  }

  public void setChoices(List<String> choices) {
    this.choices = choices;
  }

  public String getSelectedChoice() {
    return selectedChoice;
  }

  public void setSelectedChoice(String selectedChoice) {
    this.selectedChoice = selectedChoice;
  }
}
