package server.datastructure;

import server.model.Player;

public class MyGraph {

    private final Player[] players;
    private final int[][] edges;
    private int playerCount;
    private final int maxPlayers;

    public MyGraph(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        players = new Player[maxPlayers];
        edges = new int[maxPlayers][maxPlayers];
        playerCount = 0;


        for (int i = 0; i < maxPlayers; i++) {
            for (int j = 0; j < maxPlayers; j++) {
                edges[i][j] = 0;
            }
        }
    }

    public void addPlayer(Player p) {
        if (playerCount < maxPlayers) {
            players[playerCount] = p;
            playerCount++;
        }
    }

    private int getPlayerIndex(Player p) {
        for (int i = 0; i < playerCount; i++) {
            if (players[i] == p) {
                return i;
            }
        }
        return -1;
    }

    public void addTransaction(Player from, Player to, int amount) {
        int fromIndex = getPlayerIndex(from);
        int toIndex = getPlayerIndex(to);

        if (fromIndex == -1 || toIndex == -1) {
            return;
        }

        edges[fromIndex][toIndex] += amount;
    }

    public int getTotalPaid(Player p) {
        int index = getPlayerIndex(p);
        if (index == -1) return 0;

        int sum = 0;
        for (int i = 0; i < playerCount; i++) {
            sum += edges[index][i];
        }
        return sum;
    }

    public int getTotalReceived(Player p) {
        int index = getPlayerIndex(p);
        if (index == -1) return 0;

        int sum = 0;
        for (int i = 0; i < playerCount; i++) {
            sum += edges[i][index];
        }
        return sum;
    }

    public int getNetBalance(Player p) {
        return getTotalReceived(p) - getTotalPaid(p);
    }
}
