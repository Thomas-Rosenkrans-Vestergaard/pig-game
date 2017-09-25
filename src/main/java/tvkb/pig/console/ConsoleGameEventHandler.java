package tvkb.pig.console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tvkb.pig.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class ConsoleGameEventHandler implements GameEventHandler
{

	/**
	 * Runs a console version of the pig game vs the computer.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws GameStartException
	{
		ConsoleGameEventHandler consoleGameEventHandler = new ConsoleGameEventHandler(new Scanner(System.in), new PrintWriter(System.out, true));

		List<Player> players = new ArrayList<>();
		players.add(new HumanPlayer("Thomas"));
		players.add(new ComputerPlayer("Computer"));
		Game game = new Game(new DicePair(), consoleGameEventHandler, players);
		game.start();
		System.out.println("End");
	}

	/**
	 * The input of the text game.
	 */
	@NotNull private Scanner input;

	/**
	 * The output of the text game.
	 */
	@NotNull private PrintWriter output;

	/**
	 * The players currently in the game.
	 */
	@NotNull private List<Player> players = new ArrayList<>();

	/**
	 * The colors assigned to the players. Filled by onGameStart().
	 */
	@NotNull private Map<Player, String> colors = new HashMap<>();

	/**
	 * Delegates available colors when new players join.
	 */
	@NotNull private ConsoleColorDelegator colorDelegator = new ConsoleColorDelegator();

	/**
	 * The thread listening to console input.
	 */
	@Nullable private Thread decisionThread;

	/**
	 * Creates a new console game event handler.
	 *
	 * @param input  The input of the console.
	 * @param output The output to the console.
	 */
	public ConsoleGameEventHandler(@NotNull Scanner input, @NotNull PrintWriter output)
	{
		this.input = input;
		this.output = output;
	}

	/**
	 * Prints an ASCII score table to the console.
	 */
	private void printScoreTable()
	{
		output.println("---------------------------------------------------------------");
		output.println("| Name                         | Bank points                  |");
		output.println("---------------------------------------------------------------");

		players.forEach(player -> {
			output.println(String.format("| %0$-29s| %0$-29d|", player.getName(), player.getBankPoints()));
			output.println("---------------------------------------------------------------");
		});

		output.println();
	}

	/**
	 * Called when a new game starts.
	 *
	 * @param game The new game instance.
	 */
	@Override public void onGameStart(Game game)
	{
		game.getPlayers().forEach(player -> {
			players.add(player);
			colors.put(player, colorDelegator.getNextColor());
		});

		output.println("The game has started.\r\n");

		output.println(String.format("There are %d player in the game.", players.size()));
		players.forEach(player -> {
			output.print(colors.get(player));
			output.println(String.format("    %s", player.getName()));
			output.print("\u001B[0m");
		});

		printScoreTable();
	}

	/**
	 * Called when the game ends.
	 *
	 * @param game The game instance that just ended.
	 */
	@Override public void onGameEnd(Game game)
	{
		output.println("The game has ended, the winner(s) of the game is:");
		for (Player player : game.getWinners()) {
			output.println(String.format("    %s with %d points.", player.getName(), player.getTotalPoints()));
		}
	}

	/**
	 * Called when a new round starts.
	 *
	 * @param game The current game instance.
	 */
	@Override public void onRoundStart(Game game)
	{
		output.println("A new round has started.");
	}

	/**
	 * Called when a new round ends.
	 *
	 * @param game The current game instance.
	 */
	@Override public void onRoundEnd(Game game)
	{
		printScoreTable();
	}

	/**
	 * Called when a new turn begins.
	 *
	 * @param game   The current game instance.
	 * @param player The player whose turn it is.
	 */
	@Override public void onTurnStart(Game game, Player player)
	{
		output.print(colors.get(player));
		output.println(String.format("%s is up next!.", player.getName()));
	}

	/**
	 * Called when a player ends their turn.
	 *
	 * @param game   The current game instance.
	 * @param player The player whose turn ended.
	 */
	@Override public void onTurnEnd(Game game, Player player)
	{
		output.print("\u001B[0m");
	}

	/**
	 * Called when the player must make a game decision.
	 *
	 * @param game   The current game instance.
	 * @param player The player to make the decision.
	 */
	@Override public void onDecisionRequest(Game game, Player player)
	{
		output.println(String.format(
				"%s added %d points to their turn total. Current turn total is %d",
				player.getName(),
				player.getLastRoll(),
				player.getTurnPoints()
		));

		output.println(String.format("How do you want to proceed %s?", player.getName()));
		output.println("    continue");
		output.println("    save");
		output.println("    bet");

		decisionThread = new Thread(new ConsoleDecisionListener(input, output, player, game));
		decisionThread.start();
	}

	/**
	 * Called when the game has received a decision.
	 *
	 * @param game     The current game instance.
	 * @param player   The player whose decision was received.
	 * @param decision The decision that was made by the player.
	 */
	@Override public void onDecisionResponse(Game game, Player player, GameDecision decision)
	{
		if (decisionThread != null)
			decisionThread.interrupt();

		if (decision == GameDecision.CONTINUE) {
			output.println(String.format("%s continued with %d points.", player.getName(), player.getTurnPoints()));
			return;
		}

		if (decision == GameDecision.SAVE) {
			output.println(String.format("%s saved %d points.", player.getName(), player.getTurnPoints()));
			return;
		}

		if (decision == GameDecision.BET) {
			output.println(String.format("%s bet %s points.", player.getName(), player.getCurrentBet()));
			return;
		}
	}

	/**
	 * Called when a player must reset their bank.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their bank.
	 */
	@Override public void onHardReset(Game game, Player player)
	{
		output.println(String.format("%s was forced to reset their bank account losing %d points.", player.getName(), player.getTotalPoints()));
	}

	/**
	 * Called when a player must reset their turn total.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their turn total.
	 */
	@Override public void onSoftReset(Game game, Player player)
	{
		output.println(String.format("%s lost their turn and %d points.", player.getName(), player.getTurnPoints()));
	}

	/**
	 * Called when the game encounters an error.
	 *
	 * @param message The message to display.
	 */
	@Override public void onError(String message)
	{
		output.print("\u001B[31m");
		output.println(message);
		output.print("\u001B[0m");
	}
}
