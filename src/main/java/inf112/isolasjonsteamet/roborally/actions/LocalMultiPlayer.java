package inf112.isolasjonsteamet.roborally.actions;

import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class LocalMultiPlayer extends Player {

    public static int playerCount = 0;
    public ArrayList<Player> players;

    public static void newPlayer() {
        return PlayerImpl.PlayerImpl();
    }

    public boolean createPlayer(String name) {
        if (players.add(new Player(name))) {
            playerCount++;
            return true;
        }
        return false;
    }

    public void createPlayers(int quantity) {
        List<String> playerNames = new ArrayList<>(quantity);
    }

    public boolean createPlayers(List<String> playerNames) {
        if (playerNames.isEmpty()) {
            throw new IllegalArgumentException("List of player names can't be empty");
        }

        boolean allAdded = true;
        for (int i = 0; i < playerNames.size(); i++) {
            if (!createPlayer(playerNames.get(i))) {
                allAdded = false;
            }
        }
        return allAdded;
    }

    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();

        if (c == KeyEvent.VK_1){
            createPlayer("Player 1");
            }
        if (c == KeyEvent.VK_2){
            createPlayer( "Player 2");
            }
        if (c == KeyEvent.VK_3) {
            createPlayer("Player 3");
            }
        if (c == KeyEvent.VK_4){
            createPlayer("Player 4");
            }
        if (c == KeyEvent.VK_5){
            createPlayer("Player 5");
        }
        if (c == KeyEvent.VK_6) {
            createPlayer( "Player 6");
        }
        if (c == KeyEvent.VK_7) {
            createPlayer("Player 7");
        }
        if (c == KeyEvent.VK_8) {
            createPlayer( "Player 8");
        }
    }
}