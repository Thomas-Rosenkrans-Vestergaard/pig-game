package tvkb.pig;

import org.jetbrains.annotations.NotNull;

public abstract class Player
{

	/**
	 * Requests the player to make a game decision using the game.respondDecision();
	 *
	 * @param game The current game instance.
	 */
	protected abstract void requestDecision(Game game);

	/**
	 * The name of the player.
	 */
	@NotNull protected String name;

	/**
	 * The turn points the player has.
	 */
	protected int turnPoints = 0;

	/**
	 * The points the player has in the bank.
	 */
	protected int bankPoints = 0;

	/**
	 * The current amount of in the betting pot.
	 */
	protected int currentBet = 0;

	/**
	 * The players last roll.
	 */
	protected int lastRoll = 0;

	/**
	 * Creates a new player.
	 *
	 * @param name The name of the player.
	 */
	public Player(@NotNull String name)
	{
		this.name = name;
	}

	/**
	 * Rolls the provided dice.
	 *
	 * @param dice The dice to roll.
	 */
	public int roll(Dice dice)
	{
		dice.roll();
		this.lastRoll = dice.sum();

		return this.lastRoll;
	}

	/**
	 * Bets the provided amount of points.
	 *
	 * @param amount The amount of points to bet.
	 * @throws NotEnoughPointsException If the player doesn't have enough points to bet.
	 * @throws IllegalArgumentException If the provided amount is negative.
	 */
	public void bet(final int amount) throws NotEnoughPointsException, IllegalArgumentException
	{
		if (amount < 0) {
			throw new IllegalArgumentException("Bet amount cannot be less than one.");
		}

		if (this.currentBet + amount > bankPoints) {
			throw new NotEnoughPointsException("Not enough points to bet", bankPoints, this.currentBet + amount);
		}

		this.currentBet += amount;
		this.bankPoints -= amount;
	}

	/**
	 * Resolves the bets of the player.
	 *
	 * @param dice The thrown dice.
	 */
	public void resolveBet(Dice dice)
	{
		if (dice.winsBet()) {
			this.bankPoints += this.currentBet * 2;
			this.currentBet = 0;
			return;
		}

		this.currentBet = 0;
	}

	/**
	 * Returns the name of the player.
	 *
	 * @return The name of the player.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Returns the number of points in the players turn total.
	 *
	 * @return The number of points in the players turn total.
	 */
	public int getTurnPoints()
	{
		return this.turnPoints;
	}

	/**
	 * Returns the number of points in the players bank total.
	 *
	 * @return The number of points in the players bank total.
	 */
	public int getBankPoints()
	{
		return this.bankPoints;
	}

	/**
	 * Returns the total number of points in the players turn and bank.
	 *
	 * @return The total number of points in the players turn and bank.
	 */
	public int getTotalPoints()
	{
		return this.turnPoints + this.bankPoints;
	}

	/**
	 * Returns the current bet.
	 *
	 * @return The current bet.
	 */
	public int getCurrentBet()
	{
		return this.currentBet;
	}

	/**
	 * Resets the turn points of the player.
	 */
	public void resetPointsSoft()
	{
		this.turnPoints = 0;
	}

	/**
	 * Resets the bank points of the player.
	 */
	public void resetPointsHard()
	{
		if (this.bankPoints > 0)
			this.bankPoints = 0;
		this.turnPoints = 0;
	}

	/**
	 * Adds the provided amount of points to the players turn total.
	 *
	 * @param dice The dice to add to the turn total.
	 */
	public void addTurnPoints(Dice dice)
	{
		this.turnPoints += dice.sum();
	}

	/**
	 * Saves the turn points.
	 */
	public void saveTurnPoints()
	{
		this.bankPoints += this.turnPoints;
		this.turnPoints = 0;
	}

	/**
	 * Returns the last roll of the player.
	 *
	 * @return The last roll of the player.
	 */
	public int getLastRoll()
	{
		return this.lastRoll;
	}
}
