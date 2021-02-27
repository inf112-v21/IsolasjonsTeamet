package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Class to move or item forward.
 */
public class MoveForward implements Action {

	int numMoves;

	public MoveForward(int numMoves) {

		this.numMoves = numMoves;
	}

	/**
	 * Perfom an Action on the board.
	 */
	public void perform(BoardClientImpl board, Coordinate playerVec, PlayerImpl player) {
		System.out.println(player.getDir());
		switch (player.getDir()) {
			case NORTH:
				board.playerLayer.setCell(playerVec.dx, playerVec.dy + numMoves, player.playerCell);
				board.playerLayer.setCell(playerVec.dx, playerVec.dy, player.transparentCell);
				playerVec.set(playerVec.dx, playerVec.dy + numMoves);
				break;
			case EAST:
				board.playerLayer.setCell(playerVec.dx + numMoves, playerVec.dy, player.playerCell);
				board.playerLayer.setCell(playerVec.dx, playerVec.dy, player.transparentCell);
				playerVec.set(playerVec.dx + numMoves, playerVec.dy);
				break;
			case SOUTH:
				board.playerLayer.setCell(playerVec.dx, playerVec.dy - numMoves, player.playerCell);
				board.playerLayer.setCell(playerVec.dx, playerVec.dy, player.transparentCell);
				playerVec.set(playerVec.dx, playerVec.dy - numMoves);
				break;
			case WEST:
				board.playerLayer.setCell(playerVec.dx - + numMoves, playerVec.dy, player.playerCell);
				board.playerLayer.setCell(playerVec.dx, playerVec.dy, player.transparentCell);
				playerVec.set(playerVec.dx - numMoves, playerVec.dy);
				break;
			default:
				break;
		}
	}

	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(BoardClientImpl board, PlayerImpl player) {

	}
}
