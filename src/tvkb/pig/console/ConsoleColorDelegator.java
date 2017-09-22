package tvkb.pig.console;

public class ConsoleColorDelegator
{

	/**
	 * The last picked color.
	 */
	private int currentColor = 0;

	/**
	 * Returns the next available color.
	 *
	 * @return The next available color.
	 */
	public String next()
	{
		currentColor++;

		switch (currentColor) {
			case 1:
				return "\u001B[32m";
			case 2:
				return "\u001B[33m";
			case 3:
				return "\u001B[34m";
			case 4:
				return "\u001B[35m";
			case 5:
				return "\u001B[36m";
			default:
				return "\u001B[37m";
		}
	}
}
