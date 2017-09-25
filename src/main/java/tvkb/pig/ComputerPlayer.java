package tvkb.pig;

import org.jetbrains.annotations.NotNull;

public class ComputerPlayer extends Player
{

	/**
	 * Creates a new computer controlled player.
	 *
	 * @param name The name of the computer controlled player.
	 */
	public ComputerPlayer(@NotNull String name)
	{
		super(name);
	}

	/**
	 * Request a game decision from the player.
	 *
	 * @param game The current game instance.
	 */
	@Override protected void requestDecision(Game game)
	{
		if (this.turnPoints >= 21) {
			game.respondSave(this);
			return;
		}

		game.respondContinue(this);
	}
}
