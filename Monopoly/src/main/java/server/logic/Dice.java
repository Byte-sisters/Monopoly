package server.logic;

import java.util.Random;

public class Dice {
    private int d1;
    private int d2;
    private final Random rand;

    public Dice() {
        this.rand = new Random();
    }

    public int roll() {
        d1 = rand.nextInt(6) + 1;
        d2 = rand.nextInt(6) + 1;
        return d1 + d2;
    }

    public boolean isDouble() {
        return d1 == d2;
    }

    public int getD1() { return d1; }
    public int getD2() { return d2; }
}