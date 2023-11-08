package org.game.question.generation;

import org.game.question.QuestionCategory;

public class ScienceQuestionStrategy extends QuestionGenerationStrategy {

    private static final String QUESTION_TEMPLATE = "Science question %s";

    private int questionCounter = 0;

    @Override
    protected String getTemplate() {
        return QUESTION_TEMPLATE;
    }

    @Override
    protected int getQuestionCounter() {
        return questionCounter++;
    }

    @Override
    protected QuestionCategory getCategory() {
        return QuestionCategory.SCIENCE;
    }
}