package tvkb.pig;

public class BetException extends GameException
{

	/**
	 * Creates a new betting related exception.
	 *
	 * @param message The message to pass to the exception.
	 */
	public BetException(String message)
	{
		super(message);
	}
}
