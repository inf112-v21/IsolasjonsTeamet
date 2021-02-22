package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * Power Down is an Action that let's the player take a break with their robot while the game is
 * still going.
 */
public class PowerDown implements ActionImpl {

	@Override
	public void perform(BoardImpl board, PlayerImpl player) {
		//TODO
		//player.powerDown();

		System.out.println(player.getName() + " has powered down!");
	}
}
