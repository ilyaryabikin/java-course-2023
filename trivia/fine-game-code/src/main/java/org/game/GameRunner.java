package org.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.game.player.Player;
import org.game.player.PlayersProvider;
import org.game.question.generation.PopQuestionStrategy;
import org.game.question.generation.QuestionGenerationStrategy;
import org.game.question.generation.RockQuestionStrategy;
import org.game.question.generation.ScienceQuestionStrategy;
import org.game.question.generation.SportsQuestionStrategy;

@SuppressWarnings({"UncommentedMain", "MagicNumber"})
public class GameRunner {

    private GameRunner() {
    }

    public static void main(String[] args) {
        int coinsForWinAmount = 6;
        int boardFieldsCount = 12;
        WinnerChecker winnerChecker = new WinnerChecker(coinsForWinAmount);
        Dice dice = new Dice();
        Prison prison = new Prison();
        Board board = new Board(buildQuestionMap(), boardFieldsCount);
        PlayersProvider playersProvider = new PlayersProvider(registerPlayers());

        Game game = new Game(playersProvider, prison, board, dice, winnerChecker);

        game.play();
    }

    private static ArrayList<Player> registerPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Chet"));
        players.add(new Player("Pat"));
        players.add(new Player("Sue"));

        return players;
    }

    private static Map<Integer, QuestionGenerationStrategy> buildQuestionMap() {
        Map<Integer, QuestionGenerationStrategy> questionGenerationStrategyMap = new HashMap<>();
        questionGenerationStrategyMap.put(0, new PopQuestionStrategy());
        questionGenerationStrategyMap.put(1, new RockQuestionStrategy());
        questionGenerationStrategyMap.put(2, new ScienceQuestionStrategy());
        questionGenerationStrategyMap.put(3, new SportsQuestionStrategy());
        return questionGenerationStrategyMap;
    }
}
