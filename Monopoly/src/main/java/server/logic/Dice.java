package server.logic;

import java.util.Random;

public class Dice {
    private static int d1;
    private static int d2;

    public static int rollDice() {
        Random rand = new Random();
        d1 = rand.nextInt(6) + 1;
        d2 = rand.nextInt(6) + 1;
        return d1 + d2;
    }
}
