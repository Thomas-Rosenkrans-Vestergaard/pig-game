package tvkb.pig.gui;

import java.awt.*;

public class GUIColorDelegator
{

	/**
	 * The last used color.
	 */
	public int currentColor = 0;

	/**
	 * Returns the next available color.
	 *
	 * @return The next available color.
	 */
	public Color getNextColor()
	{
		currentColor++;

		switch (currentColor) {
			case 1:
				return Color.CYAN;
			case 2:
				return Color.GREEN;
			case 3:
				return Color.magenta;
			case 4:
				return Color.YELLOW;
			default:
				return Color.WHITE;
		}
	}
}
