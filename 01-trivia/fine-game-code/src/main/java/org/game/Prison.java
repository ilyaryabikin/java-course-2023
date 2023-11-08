package org.game;

public class Prison {

    public boolean canEscape(int rollResult) {
        return rollResult % 2 == 1;
    }
}
