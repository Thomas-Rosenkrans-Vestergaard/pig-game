package tvkb.pig;

import com.sun.istack.internal.NotNull;

public class DicePair implements Dice
{

	/**
	 * The first die of the pair.
	 */
	@NotNull private Die a = new Die();

	/**
	 * The second die of the pair.
	 */
	@NotNull private Die b = new Die();

	/**
	 * Rolls the dice.
	 */
	@Override public void roll()
	{
		a.roll();
		b.roll();
	}

	/**
	 * Returns the sum of the dice.
	 *
	 * @return The sum of the dice.
	 */
	@Override public int sum()
	{
		return a.getFaceValue() + b.getFaceValue();
	}

	/**
	 * Returns true if the player must now skip their turn. This returns true when one of (but not both) the dice equals 1.
	 *
	 * @return True if the player must now skip their turn.
	 */
	@Override public boolean forcesSoftReset()
	{
		return a.getFaceValue() == 1 ^ b.getFaceValue() == 1;
	}

	/**
	 * Returns true if the player must now reset their bank. This returns true when both the dice equal 1.
	 *
	 * @return True if the player must now reset their bank.
	 */
	@Override public boolean forcesHardReset()
	{
		return a.getFaceValue() == 1 && b.getFaceValue() == 1;
	}

	/**
	 * Returns true if the player wins their bet. The player wins their bet if the sum of the dice is 10 or more.
	 *
	 * @return True if the player wins their bet.
	 */
	@Override public boolean winsBet()
	{
		if (forcesSoftReset() || forcesHardReset())
			return false;

		return sum() >= 10;
	}
}
