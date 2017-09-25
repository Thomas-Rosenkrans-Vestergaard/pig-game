package tvkb.pig;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.List;

public class NameCollisionException extends GameStartException
{

	/**
	 * The players with name collisions.
	 */
	@NotNull private Map<String, List<Player>> players;

	/**
	 * Creates a new exception.
	 *
	 * @param message The message of the exception.
	 * @param players The players with name collisions.
	 */
	public NameCollisionException(@NotNull String message, @NotNull Map<String, List<Player>> players)
	{
		super(message);

		this.players = players;
	}

	/**
	 * Returns a stream of the players with name collisions.
	 *
	 * @return a stream of the players with name collisions.
	 */
	public Map<String, List<Player>> getNameCollisions()
	{
		return players;
	}
}
