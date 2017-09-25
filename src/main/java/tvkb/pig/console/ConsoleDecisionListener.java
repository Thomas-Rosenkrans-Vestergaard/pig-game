package tvkb.pig.console;

import org.jetbrains.annotations.NotNull;
import tvkb.pig.Game;
import tvkb.pig.GameDecision;
import tvkb.pig.NotEnoughPointsException;
import tvkb.pig.Player;

import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.BufferOverflowException;
import java.util.Scanner;

/**
 * Listens to the provided scanner, waiting for a decision to be made.
 */
public class ConsoleDecisionListener implements Runnable
{

	/**
	 * The scanner to listen for input on.
	 */
	@NotNull private Scanner scanner;

	/**
	 * The print writer to use when outputting to the console.
	 */
	@NotNull private PrintWriter output;

	/**
	 * The player that must input their decision.
	 */
	@NotNull private Player player;

	/**
	 * The game instance.
	 */
	@NotNull private Game game;

	/**
	 * Creates a new console decision thread.
	 *
	 * @param scanner The scanner to scan for input using.
	 * @param player  The player making the decision.
	 * @param output  The print writer to use when outputting to the console.
	 * @param game    The current game instance.
	 */
	ConsoleDecisionListener(@NotNull Scanner scanner, @NotNull PrintWriter output, @NotNull Player player, @NotNull Game game)
	{
		this.scanner = scanner;
		this.output = output;
		this.player = player;
		this.game = game;
	}

	/**
	 * Starts listening on the provided scanner.
	 */
	@Override public void run()
	{
		promptDecision();
	}

	/**
	 * Prompts the user to make a decision.
	 */
	private void promptDecision()
	{
		try {
			String input = this.scanner.nextLine();
			input = input.toUpperCase();
			input = input.trim();
			GameDecision decision = GameDecision.valueOf(input);

			if (decision == GameDecision.CONTINUE) {
				this.game.respondContinue(this.player);
				return;
			}

			if (decision == GameDecision.SAVE) {
				this.game.respondSave(player);
				return;
			}

			if (decision == GameDecision.BET) {
				promptBetAmount();
				return;
			}

		} catch (IllegalArgumentException e) {
			output.println("That doesn't make any sense.");
			promptDecision();
		} catch (IndexOutOfBoundsException | BufferOverflowException e) {
			// TODO: fix IndexOutOfBoundsException, BufferOverflowException
		}
	}

	/**
	 * Prompts the user to enter the amount of points to bet.
	 */
	private void promptBetAmount()
	{
		try {
			output.println("How much do you want to bet?");
			int amount = scanner.nextInt();
			this.game.respondBet(this.player, amount);
		} catch (NotEnoughPointsException e) {
			output.println("You do not have enough points.");
			promptBetAmount();
		} catch (IllegalArgumentException e) {
			output.println("You cannot bet a negative amount of points.");
			promptBetAmount();
		} catch (IndexOutOfBoundsException | BufferOverflowException e) {
			// TODO: fix IndexOutOfBoundsException, BufferOverflowException
		}
	}
}
