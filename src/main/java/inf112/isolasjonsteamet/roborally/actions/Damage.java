package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * Damage is an action that damages a robot x much according to the game obstacles.
 */
public class Damage implements ActionImpl {

	@Override
	public void perform(BoardImpl board, PlayerImpl player) {
		// TODO
		//player.damage();

		System.out.println(player.getName() + " has been damaged!");
	}
}
