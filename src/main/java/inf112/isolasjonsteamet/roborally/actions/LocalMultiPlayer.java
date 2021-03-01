package inf112.isolasjonsteamet.roborally.actions;

import java.security.Key;
import java.util.List;
import java.util.ArrayList;

public class LocalMultiPlayer extends Player {

    public static int playerCount = 0;
    private ArrayList<Player> players;

    public static void newPlayer() {
        return PlayerImpl.PlayerImpl();
    }

    public boolean createPlayer(int id, String name) {
        if (players.add(new Player(name, id))) {
            players++;
            playerCount++;
            return true;
        }
        return false;
    }

    public void createPlayers(int quantity) {
        List<String> playerNames = new ArrayList<>(quantity);
    }

    public boolean createPlayers(List<String> playerNames) {
        if (playerNames.isEmpty())
            throw new IllegalArgumentException("List of player names can't be empty");

        boolean allAdded = true;
        for (int i = 0; i < playerNames.size(); i++)
            if (!createPlayer(playerNames.get(i)))
                allAdded = false;

        return allAdded;
    }

}
