package tvkb.pig.console;

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
	public static void main(String[] args)
	{
		ConsoleGameEventHandler console = new ConsoleGameEventHandler(new Scanner(System.in), new PrintWriter(System.out, true));
		Game                    game    = new Game(console, new DicePair());
		game.addPlayer(new HumanPlayer("Thomas"));
		game.addPlayer(new ComputerPlayer());
		game.startGame();
	}

	/**
	 * The input of the text game.
	 */
	private Scanner input;

	/**
	 * The output of the text game.
	 */
	private PrintWriter output;

	/**
	 * The players currently in the game.
	 */
	private List<Player> players = new ArrayList<>();

	/**
	 * The colors assigned to the players.
	 */
	private Map<Player, String> colorMap = new HashMap<>();

	/**
	 * Delegates available colors when new players join.
	 */
	private ConsoleColorDelegator colorDelegator = new ConsoleColorDelegator();

	/**
	 * Creates a new console game event handler.
	 *
	 * @param input  The input of the console.
	 * @param output The output to the console.
	 */
	public ConsoleGameEventHandler(Scanner input, PrintWriter output)
	{
		assert input != null;
		assert output != null;

		this.input = input;
		this.output = output;
	}

	/**
	 * Prints an ASCII score table to the console.
	 */
	private void printScoreTable()
	{

		// Print headers.
		output.println("---------------------------------------------------------------");
		output.println("| Name                         | Bank points                  |");
		output.println("---------------------------------------------------------------");

		// Print data.
		players.forEach(player -> {
			output.println(String.format("| %0$-29s| %0$-29d|", player.getName(), player.getBankPoints()));
			output.println("---------------------------------------------------------------");
		});

		output.println();
	}

	/**
	 * Called when a new player joins.
	 *
	 * @param game   The current instance of game.
	 * @param player The player who joined.
	 */
	@Override public void onPlayerJoin(Game game, Player player)
	{
		players.add(player);
		colorMap.put(player, colorDelegator.next());
	}

	/**
	 * Called when a new game starts.
	 *
	 * @param game The new game instance.
	 */
	@Override public void onGameStart(Game game)
	{
		output.println("The game has started.\r\n");

		output.println(String.format("There are %d player in the game.", players.size()));
		players.forEach(player -> {
			output.print(colorMap.get(player));
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
	 * @param player The player whos turn it is.
	 */
	@Override public void onTurnStart(Game game, Player player)
	{
		output.print(colorMap.get(player));
		output.println(String.format("%s is up next.", player.getName()));
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
	@Override public void onDecision(Game game, Player player)
	{
		output.println(String.format(
				"%s added %d points to their turn total. Current turn total is %d",
				player.getName(),
				player.getLastRoll(),
				player.getTurnPoints()
		));

		output.println(String.format("How do you want to proceed?", player.getName()));
		output.println("    continue");
		output.println("    save");
		output.println("    bet");

		game.onResponse(player, readGameDecision(player));
	}

	/**
	 * Reads a game decision from the input.
	 *
	 * @param player The player to read from.
	 *
	 * @return The chosen decision.
	 */
	private GameDecision readGameDecision(Player player)
	{
		while (true) {
			try {
				String       decisionText = input.nextLine();
				GameDecision gameDecision = GameDecision.valueOf(decisionText.toUpperCase().trim());
				if (gameDecision != GameDecision.BET) return gameDecision;

				output.println("Please enter the amount you with to bet.");

				while (true)
					try {
						int bet = input.nextInt();
						output.println(String.format("%s put %d points on the line.", player.getName(), bet));
						player.bet(bet);
						return GameDecision.BET;
					} catch (Exception e) {
						output.println("You entered an invalid bet amount, try again.");
					}

			} catch (Exception e) {
				output.println("You entered an invalid decision, try again.");
			}
		}
	}

	/**
	 * Called when a player continues.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	@Override public void onContinue(Game game, Player player)
	{
		output.println(String.format("%s continued with %d points.", player.getName(), player.getTurnPoints()));
	}

	/**
	 * Called when a player saves.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	@Override public void onSave(Game game, Player player)
	{
		output.println(String.format("%s saved %d points.", player.getName(), player.getTurnPoints()));
	}

	/**
	 * Called when a player bets.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	@Override public void onBet(Game game, Player player)
	{
		output.println(String.format("%s bet %s points.", player.getName(), player.getBetPoints()));
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
