package tvkb.pig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class Game
{

	/**
	 * The dice to play the game with.
	 */
	@NotNull private Dice dice;

	/**
	 * Handler to send game events to.
	 */
	@NotNull private List<GameEventHandler> eventHandlers = new ArrayList<>();

	/**
	 * The players in the game.
	 */
	@NotNull private List<Player> players = new ArrayList<>();

	/**
	 * The player currently waiting to respond.
	 */
	@Nullable private Player currentRespondent;

	/**
	 * Creates a new game.
	 *
	 * @param dice The dice to play the game with.
	 */
	public Game(@NotNull Dice dice, @NotNull List<GameEventHandler> eventHandlers, @NotNull List<Player> players)
	{
		this.dice = dice;
		this.eventHandlers = eventHandlers;
		this.players = players;
	}

	public Game(@NotNull Dice dice, @NotNull GameEventHandler eventHandler, @NotNull List<Player> players)
	{
		this(dice, new ArrayList<>(), players);
		eventHandlers.add(eventHandler);
	}

	/**
	 * Starts the game.
	 *
	 * @throws GameStartException If the game could not be started.
	 */
	public void start() throws GameStartException
	{
		ensureEnoughPlayers();
		ensureNoNameCollisions();
		sendEvent(handler -> handler.onGameStart(this));
		handleTurn(players.get(0));
	}

	/**
	 * Ensures that there are at least one player in the game.
	 *
	 * @throws GameStartException When there is no players in the game.
	 */
	private void ensureEnoughPlayers() throws NotEnoughPlayersException
	{
		if (players.size() < 1) {
			eventHandlers.forEach(eventHandler -> eventHandler.onError("Cannot start game, not enough players."));
			throw new NotEnoughPlayersException("Cannot start game, not enough players.");
		}
	}

	/**
	 * Ensures that there are no name collisions in the game.
	 *
	 * @throws GameStartException When there is a name collision in the game.
	 */
	private void ensureNoNameCollisions() throws NameCollisionException
	{
		HashMap<String, List<Player>> hashMap = nameCollision();
		if (hashMap.size() > 0) {
			String message = String.format("Player name %s is not unique.", hashMap);
			sendEvent(handler -> handler.onError(message));
			throw new NameCollisionException(message, hashMap);
		}
	}

	/**
	 * Plays the turn of the provided player.
	 *
	 * @param player
	 */
	private void handleTurn(Player player)
	{
		this.currentRespondent = player;

		player.roll(dice);

		if (dice.forcesHardReset()) {
			sendEvent(handler -> handler.onHardReset(this, player));
			player.resetPointsHard();
			playNext();
			return;
		}

		if (dice.forcesSoftReset()) {
			sendEvent(handler -> handler.onSoftReset(this, player));
			player.resetPointsSoft();
			playNext();
			return;
		}

		sendEvent(handler -> handler.onTurnStart(this, player));
		player.resolveBet(dice);
		player.addTurnPoints(dice);
		player.requestDecision(this);
	}

	/**
	 * Allow a player to respond to a game decision response with continue.
	 *
	 * @param player The player who made a decision.
	 */
	public void respondContinue(Player player)
	{
		sendEvent(handler -> handler.onDecisionResponse(this, player, GameDecision.CONTINUE));
		handleTurn(player);
	}

	/**
	 * Allow a player to respond to a game decision response with save.
	 *
	 * @param player The player who made a decision.
	 */
	public void respondSave(Player player)
	{
		sendEvent(handler -> handler.onDecisionResponse(this, player, GameDecision.SAVE));
		player.saveTurnPoints();
		playNext();
	}

	/**
	 * Allow a player to respond to a game decision response with bet.
	 *
	 * @param player The player who made the decision.
	 * @param bet    The amount of points the player wants to bet.
	 * @throws NotEnoughPointsException If the player doesn't have enough points to bet.
	 * @throws IllegalArgumentException If the provided amount is negative.
	 */
	public void respondBet(Player player, int bet) throws NotEnoughPointsException, IllegalArgumentException
	{
		player.bet(bet);
		sendEvent(handler -> handler.onDecisionResponse(this, player, GameDecision.BET));
		handleTurn(player);
	}

	/**
	 * Plays the next player in the list.
	 */
	private void playNext()
	{
		sendEvent(handler -> handler.onTurnEnd(this, this.currentRespondent));

		int index = players.indexOf(this.currentRespondent);

		if (index + 1 == players.size()) {

			sendEvent(handler -> handler.onRoundEnd(this));
			Player next = players.get(0);
			if (hasWinner()) {
				sendEvent(handler -> handler.onGameEnd(this));
				return;
			}

			sendEvent(handler -> handler.onRoundStart(this));
			handleTurn(next);

		} else {

			Player next = players.get(index + 1);
			handleTurn(next);
		}
	}

	/**
	 * Sends the provided event to all the registered event handlers.
	 *
	 * @param consumer The consumer to pass the event handler to.
	 */
	private void sendEvent(final Consumer<GameEventHandler> consumer)
	{
		eventHandlers.forEach(handler -> consumer.accept(handler));
	}

	/**
	 * Delegates the provided consumer to the event handlers in the game.
	 *
	 * @param consumer The consumer to delegate to the event handlers.
	 */
	public void delegateEvent(Consumer<GameEventHandler> consumer)
	{
		eventHandlers.forEach(handler -> consumer.accept(handler));
	}

	/**
	 * Returns a HashMap of the players with name collisions.
	 *
	 * @return The HashMap of the players with name collision
	 */
	private HashMap<String, List<Player>> nameCollision()
	{
		HashMap<String, List<Player>> hashMap = new HashMap<>();
		for (Player outer : players) {
			for (Player inner : players) {
				String outerName = outer.getName();
				String innerName = inner.getName();
				if (outer != inner && outerName.equals(innerName)) {
					if (!hashMap.containsKey(outer)) hashMap.put(outerName, new ArrayList<>());
					hashMap.get(outerName).add(inner);
				}
			}
		}

		return hashMap;
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
	 * Returns a stream of the players in the game.
	 *
	 * @return A stream of the players in the game.
	 */
	public Stream<Player> getPlayers()
	{
		return players.stream();
	}
}
