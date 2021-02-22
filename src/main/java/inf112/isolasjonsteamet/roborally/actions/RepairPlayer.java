package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * Repair player is an action that repairs a player x much according to cards they recieve.
 */
public class RepairPlayer implements ActionImpl {

	@Override
	public void perform(BoardImpl board, PlayerImpl player) {
		//TODO
		//player.repair();

		System.out.println(player.getName() + " was repaired!");
	}
}
