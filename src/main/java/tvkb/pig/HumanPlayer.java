package tvkb.pig;

public class HumanPlayer extends Player
{

	/**
	 * Creates a new human player.
	 *
	 * @param name The name of the human player.
	 */
	public HumanPlayer(String name)
	{
		super(name);
	}

	/**
	 * Delegates the getNextColor game decision to the player.
	 *
	 * @param game The current game.
	 */
	@Override public void requestDecision(Game game)
	{
		game.delegateEvent(handler -> handler.onDecisionRequest(game, this));
	}
}
