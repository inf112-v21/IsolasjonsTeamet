package inf112.isolasjonsteamet.roborally.board;

/**
 * Represents the different phases of the game the board can be in.
 */
public enum Phase {
	CARDS,
	CONVEYOR_BELTS_EXPRESS,
	CONVEYOR_BELTS,
	BOARD_ELEMENTS_PUSH,
	BOARD_ELEMENTS_ROTATE,
	LASERS,
	CHECKPOINTS,
	CLEANUP,
	MISC
}
