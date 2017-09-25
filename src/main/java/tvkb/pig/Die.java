package tvkb.pig;

import java.util.Random;

public class Die
{

	/**
	 * The source of randomness in the die.
	 */
	private Random random = new Random();

	/**
	 * The face value of the die.
	 */
	private int face;

	/**
	 * Creates a new die.
	 */
	public Die()
	{
		face = getRandom();
	}

	/**
	 * Returns a random int between 1 and 6 inclusive.
	 *
	 * @return The random in between 1 and 6 inclusive.
	 */
	private int getRandom()
	{
		return random.nextInt(6) + 1;
	}

	/**
	 * Rolls the die.
	 *
	 * @return The value of the die.
	 */
	public int roll()
	{
		return this.face = getRandom();
	}

	/**
	 * Returns the face value of the die.
	 *
	 * @return The face value of the die.
	 */
	public int getFaceValue()
	{
		return face;
	}
}
