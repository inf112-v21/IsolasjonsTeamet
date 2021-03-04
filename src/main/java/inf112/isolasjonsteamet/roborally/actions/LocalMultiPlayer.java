package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public abstract class LocalMultiPlayer implements Player {

    public static int playerCount = 0;
    private String playerName;
    private int life;
    public ArrayList<Player> player;
    private Orientation direction;
    private Coordinate pos;

    /**
     * Creates a new player with the information given in the playerImpl class
     */

 //   public boolean createPlayer(String name) {
//        if (players.add(new Player(name))) {
//            playerCount++;
//            return true;
//        }
//        return false;
//}

    public void createPlayer(String playerName, Coordinate pos, Orientation orientation) {
        this.playerName = playerName;
        this.life = 5;
        this.direction = orientation;
        this.pos = pos;
    }

    public String getName() {

        return playerName;
    }

    public void createPlayers(int quantity) {
        List<String> playerNames = new ArrayList<>(quantity);
    }

    public boolean createPlayers(ArrayList<Player> playerNames) {
        if (playerNames.isEmpty()) {
            throw new IllegalArgumentException("List of player names can't be empty");
        }

        boolean allAdded = true;
        for (int i = 0; i < player.size(); i++) {
                allAdded = false;
            }
        return allAdded = true;
    }

    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();

        if (c == KeyEvent.VK_1){
            createPlayer(playerName, pos, Orientation.NORTH);
            }
        if (c == KeyEvent.VK_2){
            createPlayer(playerName, pos, Orientation.NORTH);
            }
        if (c == KeyEvent.VK_3) {
            createPlayer(playerName, pos, Orientation.NORTH);
            }
        if (c == KeyEvent.VK_4){
            createPlayer(playerName, pos, Orientation.NORTH);
            }
        if (c == KeyEvent.VK_5){
            createPlayer(playerName, pos, Orientation.NORTH);
        }
        if (c == KeyEvent.VK_6) {
            createPlayer(playerName, pos, Orientation.NORTH);
        }
        if (c == KeyEvent.VK_7) {
            createPlayer(playerName, pos, Orientation.NORTH);
        }
        if (c == KeyEvent.VK_8) {
            createPlayer(playerName, pos, Orientation.NORTH);
        }
    }
}