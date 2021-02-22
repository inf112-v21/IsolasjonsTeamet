package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * Kill robot is an action that let's a player kill a robot or the main master robot.
 *
 */
public class KillRobot implements ActionImpl {

	@Override
	public void perform(BoardImpl board, PlayerImpl player) {
		//TODO
		//player.kill();

		System.out.println(player.getName() + " has been killed!");
	}
}
