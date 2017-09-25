package tvkb.pig;

public interface Dice
{

	/**
	 * Rolls the dice.
	 */
	void roll();

	/**
	 * Returns the sum of the dice.
	 *
	 * @return The sum of the dice.
	 */
	int sum();

	/**
	 * Returns true if the player must now skip their turn and lose their turn points.
	 *
	 * @return True if the player must now skip their turn and lose their turn points.
	 */
	boolean forcesSoftReset();

	/**
	 * Returns true if the player must now reset their bank and turn points.
	 *
	 * @return True if the player must now reset their bank and turn points.
	 */
	boolean forcesHardReset();

	/**
	 * Returns true if the player wins their bet based on the dice.
	 *
	 * @return True if the player wins their bet based on the dice.
	 */
	boolean winsBet();
}
