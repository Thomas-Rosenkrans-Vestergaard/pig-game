package tvkb.pig;

import com.sun.istack.internal.NotNull;

public interface GameEventHandler
{

	/**
	 * Called when a new player joins.
	 *
	 * @param game   The current instance of game.
	 * @param player The player who joined.
	 */
	void onPlayerJoin(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a new game starts.
	 *
	 * @param game The new game instance.
	 */
	void onGameStart(@NotNull Game game);

	/**
	 * Called when the game ends.
	 *
	 * @param game The game instance that just ended.
	 */
	void onGameEnd(@NotNull Game game);

	/**
	 * Called when a new round starts.
	 *
	 * @param game The current game instance.
	 */
	void onRoundStart(@NotNull Game game);

	/**
	 * Called when a new round ends.
	 *
	 * @param game The current game instance.
	 */
	void onRoundEnd(@NotNull Game game);

	/**
	 * Called when a new turn begins.
	 *
	 * @param game   The current game instance.
	 * @param player The player whos turn it is.
	 */
	void onTurnStart(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a player ends their turn.
	 *
	 * @param game   The current game instance.
	 * @param player The player whos turn ended.
	 */
	void onTurnEnd(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a player must make a game decision.
	 *
	 * @param game   The current game instance.
	 * @param player The player to make the decision.
	 */
	void onDecision(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a player continues.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	void onContinue(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a player saves.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	void onSave(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a player bets.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	void onBet(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a player must reset their turn and bank total.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their bank.
	 */
	void onHardReset(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when a player must reset their turn total.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their turn total.
	 */
	void onSoftReset(@NotNull Game game, @NotNull Player player);

	/**
	 * Called when the game encounters an error.
	 *
	 * @param message The message to display.
	 */
	void onError(@NotNull String message);
}