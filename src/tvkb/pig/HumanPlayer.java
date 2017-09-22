package tvkb.pig;

import com.sun.istack.internal.NotNull;

public class HumanPlayer extends Player
{

	/**
	 * Creates a new human player.
	 *
	 * @param name The name of the human player.
	 */
	public HumanPlayer(@NotNull String name)
	{
		super(name);
	}

	/**
	 * Delegates the next game decision to the player.
	 *
	 * @param game The current game.
	 */
	@Override public void requestDecision(Game game)
	{
		game.getEventHandler().onDecision(game, this);
	}
}
