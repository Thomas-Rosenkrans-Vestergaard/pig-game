package tvkb.pig.gui;

import com.sun.istack.internal.NotNull;
import tvkb.pig.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class GUIGameEventHandler implements GameEventHandler
{

	/**
	 * Runs the GUI version of the pig game vs the computer.
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		GUIGameEventHandler guiGameEventHandler = new GUIGameEventHandler();
		Game                game                = new Game(guiGameEventHandler, new DicePair());

		JFrame frame = new JFrame("GUIGameEventHandler");
		frame.setContentPane(guiGameEventHandler.main);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(1366, 768);
		frame.setVisible(true);

		game.addPlayer(new HumanPlayer("Thomas"));
		game.addPlayer(new ComputerPlayer());
		game.startGame();
	}

	/**
	 * The main panel of the GUI.
	 */
	private JPanel main;

	/**
	 * The player table in the GUI.
	 */
	private JTable table;

	/**
	 * The text pane in the GUI.
	 */
	private JTextPane textPane;

	/**
	 * The 'Bet' button in the GUI.
	 */
	private JButton betButton;

	/**
	 * The 'Continue' button in the GUI.
	 */
	private JButton continueButton;

	/**
	 * The 'Save' button in the GUI.
	 */
	private JButton saveButton;

	/**
	 * The last ActionListener that was added to the bet button.
	 */
	private ActionListener betButtonListener;

	/**
	 * The last ActionListener that was added to the continue button.
	 */
	private ActionListener continueButtonListener;

	/**
	 * The last ActionListener that was added to the save button.
	 */
	private ActionListener saveButtonListener;

	/**
	 * The players in the game.
	 */
	@NotNull private List<Player> players = new ArrayList<>();

	/**
	 * Map of the players and their color.
	 */
	@NotNull private Map<Player, Color> colorMap = new HashMap<>();

	/**
	 * Delegates available colors to the new players that join.
	 */
	@NotNull private GUIColorDelegator colorDelegator = new GUIColorDelegator();

	/**
	 * Creates a new GUI pig game.
	 */
	public GUIGameEventHandler()
	{
		disableDecisionButtons();
		textPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
	}

	/**
	 * Disables the buttons in the GUI.
	 */
	private void disableDecisionButtons()
	{
		betButton.setEnabled(false);
		continueButton.setEnabled(false);
		saveButton.setEnabled(false);
	}

	/**
	 * Enables the buttons in the GUI.
	 */
	private void enableDecisionButtons()
	{
		betButton.setEnabled(true);
		continueButton.setEnabled(true);
		saveButton.setEnabled(true);
	}

	/**
	 * Adds listeners to the decision buttons.
	 */
	private void addDecisionButtonListeners(@NotNull final Game game, @NotNull final Player player)
	{
		this.continueButtonListener = e -> respondToDecisionRequest(game, player, GameDecision.CONTINUE);
		this.saveButtonListener = e -> respondToDecisionRequest(game, player, GameDecision.SAVE);
		this.betButtonListener = e -> respondToDecisionRequest(game, player, GameDecision.BET);

		continueButton.addActionListener(continueButtonListener);
		saveButton.addActionListener(saveButtonListener);
		betButton.addActionListener(betButtonListener);
	}

	/**
	 * Returns the action listeners from the decision buttons.
	 */
	private void removeDecisionButtonListeners()
	{
		assert continueButtonListener != null;
		assert saveButtonListener != null;
		assert betButtonListener != null;

		continueButton.removeActionListener(continueButtonListener);
		saveButton.removeActionListener(saveButtonListener);
		betButton.removeActionListener(betButtonListener);
	}

	/**
	 * Updates the players table.
	 */
	private void updatePlayerTable()
	{
		Vector columns = new Vector();
		columns.add("Name");
		columns.add("Turn points");
		columns.add("Bank points");

		Vector rows = new Vector();
		for (Player player : players) {
			Vector row = new Vector();
			row.add(player.getName());
			row.add(player.getTurnPoints());
			row.add(player.getBankPoints());
			rows.add(row);
		}

		this.table.setModel(new DefaultTableModel(rows, columns));
	}

	/**
	 * Writes the provided string to the text pane.
	 *
	 * @param output The string output to write.
	 */
	private void writeToTextPane(@NotNull String output)
	{
		try {
			StyledDocument document = textPane.getStyledDocument();
			Style          style    = textPane.addStyle("x", null);
			StyleConstants.setForeground(style, Color.WHITE);
			document.insertString(document.getLength(), output + "\r\n", style);
		} catch (Exception e) {
			onError(e.getMessage());
		}
	}

	/**
	 * Writes the provided string to the text pane.
	 *
	 * @param output The string output to write.
	 * @param color  The color to write.
	 */
	private void writeToTextPane(@NotNull String output, @NotNull Color color)
	{
		try {
			StyledDocument document = textPane.getStyledDocument();
			Style          style    = textPane.addStyle("x", null);
			StyleConstants.setForeground(style, color);
			document.insertString(document.getLength(), output + "\r\n", style);
		} catch (Exception e) {
			onError(e.getMessage());
		}
	}

	/**
	 * Called when a new player joins.
	 *
	 * @param game   The current instance of game.
	 * @param player The player who joined.
	 */
	@Override public void onPlayerJoin(Game game, Player player)
	{
		this.players.add(player);
		this.colorMap.put(player, colorDelegator.next());
		updatePlayerTable();

		writeToTextPane(String.format("Player %s has joined the game.", player.getName()));
	}

	/**
	 * Called when a new game starts.
	 *
	 * @param game The new game instance.
	 */
	@Override public void onGameStart(Game game)
	{
		writeToTextPane("The game has begun.");
	}

	/**
	 * Called when the game ends.
	 *
	 * @param game The game instance that just ended.
	 */
	@Override public void onGameEnd(Game game)
	{
		StringBuilder message = new StringBuilder();
		message.append("The game has ended, the winner(s) of the game is:\r\n");
		for (Player player : game.getWinners()) {
			message.append(String.format("    %s with %d points.", player.getName(), player.getTotalPoints()));
		}

		writeToTextPane(message.toString());

		JOptionPane.showMessageDialog(main, message.toString());
	}

	/**
	 * Called when a new round starts.
	 *
	 * @param game The current game instance.
	 */
	@Override public void onRoundStart(Game game)
	{

	}

	/**
	 * Called when a new round ends.
	 *
	 * @param game The current game instance.
	 */
	@Override public void onRoundEnd(Game game)
	{

	}

	/**
	 * Called when a new turn begins.
	 *
	 * @param game   The current game instance.
	 * @param player The player whose turn it is.
	 */
	@Override public void onTurnStart(Game game, Player player)
	{
		updatePlayerTable();
	}

	/**
	 * Called when a player ends their turn.
	 *
	 * @param game   The current game instance.
	 * @param player The player whose turn ended.
	 */
	@Override public void onTurnEnd(Game game, Player player)
	{
		updatePlayerTable();
	}

	/**
	 * Called when the player must make a game decision.
	 *
	 * @param game   The current game instance.
	 * @param player The player to make the decision.
	 */
	@Override public void onDecision(final Game game, final Player player)
	{
		updatePlayerTable();

		Color playerColor = colorMap.get(player);

		writeToTextPane(String.format(
				"%s added %d points to their turn total. Current turn total is %d.",
				player.getName(),
				player.getLastRoll(),
				player.getTurnPoints()
		), playerColor);

		writeToTextPane("You can now decide what to do next!", playerColor);

		enableDecisionButtons();
		addDecisionButtonListeners(game, player);
	}

	/**
	 * Responds to the game, with the players decision.
	 *
	 * @param game     The current game instance.
	 * @param player   The player.
	 * @param decision The decision.
	 */
	private void respondToDecisionRequest(Game game, Player player, GameDecision decision)
	{
		if (decision == GameDecision.BET) {
			String message = "How much are you willing to bet?";
			while (true) try {
				String input  = JOptionPane.showInputDialog(message);
				int    amount = Integer.parseInt(input);
				player.bet(amount);
				break;
			} catch (Exception e) {
				message = "An error occurred, try again.";
			}
		}

		disableDecisionButtons();
		removeDecisionButtonListeners();
		game.onResponse(player, decision);
	}

	/**
	 * Called when a player continues.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	@Override public void onContinue(Game game, Player player)
	{
		writeToTextPane(String.format("%s continued with %d points.", player.getName(), player.getTurnPoints()), colorMap.get(player));
	}

	/**
	 * Called when a player saves.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	@Override public void onSave(Game game, Player player)
	{
		writeToTextPane(String.format("%s saved %d points.", player.getName(), player.getTurnPoints()), colorMap.get(player));
	}

	/**
	 * Called when a player bets.
	 *
	 * @param game   The current game instance.
	 * @param player The player who made the decision.
	 */
	@Override public void onBet(Game game, Player player)
	{
		writeToTextPane(String.format("%s bet %s points.", player.getName(), player.getBetPoints()), colorMap.get(player));
	}

	/**
	 * Called when a player must reset their bank.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their bank.
	 */
	@Override public void onHardReset(Game game, Player player)
	{
		writeToTextPane(String.format("%s was forced to reset their bank account losing %d points.", player.getName(), player.getTotalPoints()), colorMap.get(player));
	}

	/**
	 * Called when a player must reset their turn total.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their turn total.
	 */
	@Override public void onSoftReset(Game game, Player player)
	{
		writeToTextPane(String.format("%s lost their turn and %d points.", player.getName(), player.getTurnPoints()), colorMap.get(player));
	}

	/**
	 * Called when the game encounters an error.
	 *
	 * @param message The message to display.
	 */
	@Override public void onError(String message)
	{
		writeToTextPane(message, Color.RED);
	}
}
