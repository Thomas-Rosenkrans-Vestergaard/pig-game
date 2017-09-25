package tvkb.pig;

import org.jetbrains.annotations.NotNull;

public class NotEnoughPlayersException extends GameStartException
{

	/**
	 * Creates a new exception.
	 *
	 * @param message The message of the exception.
	 */
	public NotEnoughPlayersException(@NotNull String message)
	{
		super(message);
	}
}
