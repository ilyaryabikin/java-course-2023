package org.game;

import org.game.player.Player;

public class WinnerChecker {

    private final int coinsAmountForWin;

    public WinnerChecker(int goldAmountForWin) {
        this.coinsAmountForWin = goldAmountForWin;
    }

    public boolean isPlayerWinner(Player player) {
        return player.getCoins() == coinsAmountForWin;
    }
}
