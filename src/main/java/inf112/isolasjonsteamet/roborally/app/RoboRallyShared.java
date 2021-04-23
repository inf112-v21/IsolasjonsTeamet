package inf112.isolasjonsteamet.roborally.app;

import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.ConveyorBeltTile;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import java.util.function.BiConsumer;

/**
 * Holds common code for the client and server.
 */
public abstract class RoboRallyShared implements ActionProcessor {

	/**
	 * Performs an action for each tile each player is standing on.
	 */
	protected abstract void foreachPlayerTile(BiConsumer<Player, Tile> handler);

	/** Access to the board. */
	protected abstract Board board();

	/** Signals what the current phase is. */
	protected abstract void setCurrentPhase(Phase phase);

	/**
	 * Processes all the tiles the players are standing on with the given phase.
	 */
	public void processPlayerTiles(Phase phase) {
		setCurrentPhase(phase);
		foreachPlayerTile((player, tile) -> {
			for (Action action : tile.getActions()) {
				performActionNow(player.getRobot(), action, phase);
			}
		});
	}

	protected void processBoardElements() {
		processPlayerTiles(Phase.CONVEYOR_BELTS_EXPRESS);
		processPlayerTiles(Phase.CONVEYOR_BELTS);
		processPlayerTiles(Phase.BOARD_ELEMENTS_PUSH);
		processPlayerTiles(Phase.BOARD_ELEMENTS_ROTATE);
		processPlayerTiles(Phase.LASERS);
		processPlayerTiles(Phase.CHECKPOINTS);
	}

	protected void processCleanup() {
		processPlayerTiles(Phase.CLEANUP);
	}
}
