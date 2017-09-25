package tvkb.pig;

public class NotEnoughPointsException extends BetException
{

	/**
	 * The amount of points in the players bank.
	 */
	final public int bankPoints;

	/**
	 * The amount of points that were tried to bet.
	 */
	final public int betAmount;

	/**
	 * @param message
	 */
	public NotEnoughPointsException(String message, int bankPoints, int betAmount)
	{
		super(message);

		this.bankPoints = bankPoints;
		this.betAmount = betAmount;
	}
}
