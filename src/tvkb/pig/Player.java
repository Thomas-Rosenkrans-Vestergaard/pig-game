package tvkb.pig;

public abstract class Player
{

	/**
	 * Prompts the player to make a game decision.
	 *
	 * @param game The current game instance.
	 */
	protected abstract void requestDecision(Game game);

	/**
	 * The name of the player.
	 */
	protected String name;

	/**
	 * The turn points the player has.
	 */
	protected int turnPoints = 0;

	/**
	 * The points the player has in the bank.
	 */
	protected int bankPoints = 0;

	/**
	 * The current amount of points bet.
	 */
	protected int betPoints = 0;

	/**
	 * The players last roll.
	 */
	protected int lastRoll = 0;

	/**
	 * Creates a new player.
	 *
	 * @param name The name of the player.
	 */
	public Player(String name)
	{
		assert name != null;
		this.name = name;
	}

	/**
	 * Rolls the provided dice.
	 *
	 * @param dice The dice to roll.
	 */
	public void roll(Dice dice)
	{
		dice.roll();
		lastRoll = dice.sum();
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
	 * Handles the player winning their bet.
	 */
	public void onBetWin()
	{
		bankPoints += betPoints;
		betPoints = 0;
	}

	/**
	 * Handles the player losing their bet.
	 */
	public void onBetLoss()
	{
		bankPoints -= betPoints;
		betPoints = 0;
	}

	/**
	 * Bets the provided amount of points.
	 *
	 * @param bet The bet to make.
	 */
	public void bet(int bet)
	{
		betPoints += bet;
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
	public int getBetPoints()
	{
		return this.betPoints;
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
		this.bankPoints = 0;
		this.turnPoints = 0;
	}

	/**
	 * Adds the provided amount of points to the players turn total.
	 *
	 * @param add The amount of points to add to the players turn total.
	 */
	public void addTurnPoints(int add)
	{
		this.turnPoints += add;
	}

	/**
	 * Adds the provided amount of points to the players bank total.
	 *
	 * @param add The amount of points to add to the players bank total.
	 */
	public void addBankPoints(int add)
	{
		this.bankPoints += add;
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
