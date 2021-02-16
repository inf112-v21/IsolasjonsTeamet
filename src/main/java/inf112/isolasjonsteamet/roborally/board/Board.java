package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import java.util.List;
import javax.annotation.Nullable;

public interface Board {

	List<Player> getPlayers();

	@Nullable
	Player getPlayerAt(Vector2 pos);

	List<TileType> getTilesAt(Vector2 pos);

}
