package org.game.question;

@SuppressWarnings("MagicNumber")
public class Question {

    private final String question;
    private final int answer;
    private final QuestionCategory category;
    private final int reward;

    public Question(String question, int answer, QuestionCategory category, int reward) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.reward = reward;
    }

    public boolean checkAnswer(int playerAnswer) {
        return this.answer % 3 == playerAnswer % 3;
    }

    public String getQuestion() {
        return question;
    }

    public QuestionCategory getCategory() {
        return category;
    }

    public int getReward() {
        return reward;
    }
}
