package tvkb.pig;

public class ComputerPlayer extends Player
{

	/**
	 * Creates a new computer player.
	 */
	public ComputerPlayer()
	{
		super("Computer");
	}

	/**
	 * Request a game decision from the player.
	 *
	 * @param game The current game instance.
	 */
	@Override protected void requestDecision(Game game)
	{
		game.onResponse(this, this.turnPoints >= 21 ? GameDecision.SAVE : GameDecision.CONTINUE);
	}
}
