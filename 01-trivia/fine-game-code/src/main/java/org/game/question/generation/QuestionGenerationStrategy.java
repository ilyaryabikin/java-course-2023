package org.game.question.generation;

import java.util.Random;
import org.game.question.Question;
import org.game.question.QuestionCategory;

public abstract class QuestionGenerationStrategy {

    private static final Random RANDOMIZER = new Random();
    private static final int RANDOM_ANSWER_BOUND = 10;
    private static final int QUESTION_REWARD = 1;

    public Question getQuestion() {
        String question = getTemplate().formatted(getQuestionCounter());
        int answer = RANDOMIZER.nextInt(RANDOM_ANSWER_BOUND);
        QuestionCategory category = getCategory();

        return new Question(question, answer, category, QUESTION_REWARD);
    }

    protected abstract String getTemplate();

    protected abstract int getQuestionCounter();

    protected abstract QuestionCategory getCategory();
}
