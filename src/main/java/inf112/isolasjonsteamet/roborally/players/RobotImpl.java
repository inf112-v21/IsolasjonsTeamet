package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.KillRobot;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Robot class that holds robot methods.
 */
public class RobotImpl implements Robot {

	private static int instances = 0;

	private final ActionProcessor actionProcessor;

	private Orientation direction;
	private int life;
	private int damageToken;
	private Coordinate pos;

	private final int instanceNum;

	/**
	 * Constructor of a new player.
	 */
	public RobotImpl(ActionProcessor actionProcessor, Coordinate pos, Orientation orientation) {
		this.actionProcessor = actionProcessor;
		this.life = 5;
		this.direction = orientation;
		this.pos = pos;
		this.damageToken = 0;
		this.instanceNum = instances++;
	}

	/**
	 * Get the position of a robot.
	 */
	@Override
	public Coordinate getPos() {
		return pos;
	}

	@Override
	public void setPos(Coordinate pos) {
		this.pos = pos;
	}

	/**
	 * Move the robot on the board.
	 */
	@Override
	public void move(Coordinate offset) {
		pos = pos.add(offset);
	}


	@Override
	public boolean checkWinCondition(Board board) {
		return board.getTilesAt(pos).contains(Tiles.FLAG);
	}

	@Override
	public boolean checkLossCondition(Board board) {
		return board.getTilesAt(pos).contains(Tiles.HOLE);
	}

	/**
	 * Gets direction of the robot.
	 *
	 * @return direction
	 */
	@Override
	public Orientation getDir() {
		return direction;
	}

	/**
	 * Sets direction of the robot.
	 */
	@Override
	public void setDir(Orientation dir) {
		this.direction = dir;
	}

	@Override
	public void damageRobot() {
		if (++this.damageToken >= 10) {
			actionProcessor.performActionNow(this, new KillRobot());
		}
	}

	@Override
	public void repairRobot() {
		if (damageToken == 0) {
			throw new IllegalStateException("Can not get negative damage tokens");
		} else {
			this.damageToken -= 1;
		}
	}

	@Override
	public void killRobot() {
		life -= 1;
		damageToken = 0;
	}

	public int getLife() {
		return life;
	}

	public int getDamageTokens() {
		return damageToken;
	}

	@Override
	public String getDebugName() {
		return "Robot" + instanceNum;
	}
}
