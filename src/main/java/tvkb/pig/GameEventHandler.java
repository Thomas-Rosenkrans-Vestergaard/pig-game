package tvkb.pig;

public interface GameEventHandler
{

	/**
	 * Called when a new game starts.
	 *
	 * @param game The new game instance.
	 */
	void onGameStart(Game game);

	/**
	 * Called when the game ends.
	 *
	 * @param game The game instance that just ended.
	 */
	void onGameEnd(Game game);

	/**
	 * Called when a new round starts.
	 *
	 * @param game The current game instance.
	 */
	void onRoundStart(Game game);

	/**
	 * Called when a new round ends.
	 *
	 * @param game The current game instance.
	 */
	void onRoundEnd(Game game);

	/**
	 * Called when a new turn begins.
	 *
	 * @param game   The current game instance.
	 * @param player The player whose turn it is.
	 */
	void onTurnStart(Game game, Player player);

	/**
	 * Called when a player ends their turn.
	 *
	 * @param game   The current game instance.
	 * @param player The player whose turn ended.
	 */
	void onTurnEnd(Game game, Player player);

	/**
	 * Called when a player must make a game decision.
	 *
	 * @param game   The current game instance.
	 * @param player The player to make the decision.
	 */
	void onDecisionRequest(Game game, Player player);

	/**
	 * Called when the game has received a decision.
	 *
	 * @param game     The current game instance.
	 * @param player   The player whose decision was received.
	 * @param decision The decision that was made by the player.
	 */
	void onDecisionResponse(Game game, Player player, GameDecision decision);

	/**
	 * Called when a player must reset their turn and bank total.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their bank.
	 */
	void onHardReset(Game game, Player player);

	/**
	 * Called when a player must reset their turn total.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their turn total.
	 */
	void onSoftReset(Game game, Player player);

	/**
	 * Called when the game encounters an error.
	 *
	 * @param message The message to display.
	 */
	void onError(String message);
}
