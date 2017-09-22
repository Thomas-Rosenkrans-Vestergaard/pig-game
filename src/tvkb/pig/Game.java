package tvkb.pig;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Game
{

	/**
	 * Handler to send game events to.
	 */
	@NotNull private GameEventHandler eventHandler;

	/**
	 * The dice to play the game with.
	 */
	@NotNull private Dice dice;

	/**
	 * The players in the game.
	 */
	@NotNull private List<Player> players = new ArrayList<>();

	/**
	 * The player currently waiting to respond.
	 */
	@NotNull private Player currentRespondent;

	/**
	 * Creates a new game.
	 *
	 * @param eventHandler The object to send game events to.
	 * @param dice         The dice to play the game with.
	 */
	public Game(@NotNull GameEventHandler eventHandler, @NotNull Dice dice)
	{
		assert eventHandler != null;
		assert dice != null;

		this.eventHandler = eventHandler;
		this.dice = dice;
	}

	/**
	 * Starts the game.
	 */
	public void startGame()
	{

		// Check that there is enough players in the game.
		if (players.size() < 1) {
			eventHandler.onError("Cannot start game, not enough players.");
			return;
		}

		// Checks that the names of the players are unique.
		String nameCollision = nameCollision();
		if (nameCollision != null) {
			eventHandler.onError(String.format("Player name %s is not unique.", nameCollision));
		}

		eventHandler.onGameStart(this);

		this.currentRespondent = players.get(0);
		eventHandler.onTurnStart(this, this.currentRespondent);
		playTurn(this.currentRespondent);
	}

	/**
	 * Plays the turn of the provided player.
	 *
	 * @param player The player to startGame.
	 */
	private void playTurn(Player player)
	{
		player.roll(dice);
		int points = dice.sum();

		// Handles hard resets.
		if (dice.forcesHardReset()) {
			eventHandler.onHardReset(this, player);
			player.resetPointsHard();
			player.resetPointsSoft();
			playNext(player);
			return;
		}

		// Handles soft rests.
		if (dice.forcesSoftReset()) {
			eventHandler.onSoftReset(this, player);
			player.resetPointsSoft();
			playNext(player);
			return;
		}

		// Handles betting results
		if (dice.winsBet()) {
			player.onBetWin();
		} else {
			player.onBetLoss();
		}

		player.addTurnPoints(points);
		this.currentRespondent = player;
		player.requestDecision(this);
	}

	/**
	 * Retrieves a decision response from a player.
	 *
	 * @param player   The player who responded.
	 * @param decision The decision the player made.
	 */
	public void onResponse(Player player, GameDecision decision)
	{
		if (player != this.currentRespondent)
			return;

		if (decision == GameDecision.CONTINUE) {
			eventHandler.onContinue(this, player);
			playTurn(player);
		}

		if (decision == GameDecision.BET) {
			eventHandler.onBet(this, player);
			playTurn(player);
			return;
		}

		if (decision == GameDecision.SAVE) {
			eventHandler.onSave(this, player);
			int turnPoints = player.getTurnPoints();
			player.addBankPoints(turnPoints);
			player.resetPointsSoft();
			playNext(player);
		}
	}

	/**
	 * Plays the next player in the list.
	 */
	private void playNext(@NotNull Player player)
	{
		eventHandler.onTurnEnd(this, player);
		int index = players.indexOf(player);

		// If the player is the last in the list.
		if (index + 1 == players.size()) {

			// Signal a round end.
			eventHandler.onRoundEnd(this);
			Player next = players.get(0);

			// Check if a winner exists.
			if (hasWinner()) {
				eventHandler.onGameEnd(this);
				return;
			}

			// Signal a round end.
			eventHandler.onRoundStart(this);
			eventHandler.onTurnStart(this, next);

			// Play the next player.
			playTurn(next);
		} else {

			// Play the next player in the list.
			Player next = players.get(index + 1);
			eventHandler.onTurnStart(this, next);
			playTurn(next);
		}
	}

	/**
	 * Returns a non-null string if there exists a name collision between the players in the game.
	 *
	 * @return The name collision.
	 */
	private String nameCollision()
	{
		for (Player outer : players)
			for (Player inner : players)
				if (outer != inner && outer.getName().equals(inner.getName()))
					return outer.getName();

		return null;
	}

	/**
	 * Returns true if there exists a winner to the current game.
	 *
	 * @return True if there exists a winner to the current game.
	 */
	private boolean hasWinner()
	{
		for (Player player : this.players)
			if (player.getTotalPoints() >= 100)
				return true;

		return false;
	}

	/**
	 * Returns a list of the winners of the game. The winner is the player(s) with the most points.
	 *
	 * @return A list of the winners of the game. The winner is the player(s) with the most points.
	 */
	public List<Player> getWinners()
	{
		List<Player> players = new ArrayList<>(this.players);

		// Sort the players.
		players.sort((a, b) -> Integer.compare(b.getTotalPoints(), a.getTotalPoints()));
		int firstTotal = players.get(0).getTotalPoints();

		if (players.size() < 2)
			return players;

		// Return the sublist from the first element to the first element that doesn't at least go even on points.
		for (int i = 1; i < players.size(); i++) {
			if (firstTotal != players.get(i).getTotalPoints()) {
				return players.subList(0, i);
			}
		}

		return players;
	}

	/**
	 * Adds a new player to the game.
	 *
	 * @param player The player to add to the game.
	 */
	public void addPlayer(Player player)
	{
		eventHandler.onPlayerJoin(this, player);

		this.players.add(player);
	}

	/**
	 * Returns the event handler.
	 *
	 * @return The event handler.
	 */
	public GameEventHandler getEventHandler()
	{
		return this.eventHandler;
	}
}
