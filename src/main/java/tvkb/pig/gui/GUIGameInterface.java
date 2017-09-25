package tvkb.pig.gui;

import tvkb.pig.*;
import tvkb.pig.console.ConsoleGameInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class GUIGameInterface extends GameGUI implements GameEventHandler
{

	/**
	 * Runs the GUI version of the pig game vs the computer.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws GameStartException
	{
		GUIGameInterface     guiGameInterface     = new GUIGameInterface();
		ConsoleGameInterface consoleGameInterface = new ConsoleGameInterface(new Scanner(System.in), new PrintWriter(System.out, true));

		List<Player> players = new ArrayList<>();
		players.add(new HumanPlayer("Human"));
		players.add(new ComputerPlayer("Computer"));

		List<GameEventHandler> eventHandlers = new ArrayList<>();
		eventHandlers.add(guiGameInterface);
		eventHandlers.add(consoleGameInterface);

		Game game = new Game(new DicePair(), eventHandlers, players);
		game.start();
	}

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
	private List<Player> players = new ArrayList<>();

	/**
	 * Map of the players and their color.
	 */
	private Map<Player, Color> colors = new HashMap<>();

	/**
	 * Delegates available colors to the new players that join.
	 */
	private GUIColorDelegator colorDelegator = new GUIColorDelegator();

	/**
	 * Creates a new GUI pig game.
	 */
	public GUIGameInterface()
	{
		JFrame frame = new JFrame("Pig Game");
		frame.setContentPane(this.main);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(1366, 768);
		frame.setVisible(true);

		disableDecisionButtons();
		textPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		addSliderListener();
	}

	/**
	 * Disables the buttons in the GUI.
	 */
	private void disableDecisionButtons()
	{
		continueButton.setEnabled(false);
		saveButton.setEnabled(false);
		betButton.setEnabled(false);
	}

	/**
	 * Enables the buttons in the GUI.
	 */
	private void enableDecisionButtons()
	{
		continueButton.setEnabled(true);
		saveButton.setEnabled(true);
		updateBetButton();
	}

	/**
	 * Updates the bet button, setting enabled to true if the value of the bet slider is positive.
	 */
	private void updateBetButton()
	{
		betButton.setEnabled(betSlider.getValue() != 0);
	}

	/**
	 * Adds listeners to the decision buttons.
	 */
	private void addDecisionButtonListeners(final Game game, final Player player)
	{
		this.continueButtonListener = e -> game.respondContinue(player);
		this.saveButtonListener = e -> game.respondSave(player);
		this.betButtonListener = e -> handleBet(game, player);

		continueButton.addActionListener(continueButtonListener);
		saveButton.addActionListener(saveButtonListener);
		betButton.addActionListener(betButtonListener);
	}

	/**
	 * Returns the action listeners from the decision buttons.
	 */
	private void removeDecisionButtonListeners()
	{
		continueButton.removeActionListener(continueButtonListener);
		saveButton.removeActionListener(saveButtonListener);
		betButton.removeActionListener(betButtonListener);
	}

	/**
	 * Updates the slider max value.
	 *
	 * @param player The provided
	 */
	private void updateSlider(Player player)
	{
		int currentValue = this.betSlider.getValue();
		int bankPoints   = player.getBankPoints();

		if (currentValue > bankPoints)
			this.betSlider.setValue(bankPoints);

		this.betSlider.setMaximum(bankPoints);
		this.betSlider.setEnabled(bankPoints != 0);
		this.betSlider.setMinimum(0);
	}

	/**
	 * Adds a change listener to the slider.
	 */
	private void addSliderListener()
	{
		this.betSlider.addChangeListener(event -> {
			int value = this.betSlider.getValue();
			betLabel.setText(String.valueOf(value));
			updateBetButton();
		});
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
	private void writeToTextPane(String output)
	{
		try {
			StyledDocument     document   = textPane.getStyledDocument();
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setForeground(attributes, Color.WHITE);
			document.insertString(document.getLength(), output + "\r\n", attributes);
		} catch (Throwable e) {
			// TODO: fix java.lang.Error: Interrupted attempt to acquire write lock
		}
	}

	/**
	 * Writes the provided string to the text pane.
	 *
	 * @param output The string output to write.
	 * @param color  The color to write.
	 */
	private void writeToTextPane(String output, Color color)
	{
		try {
			StyledDocument     document   = textPane.getStyledDocument();
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setForeground(attributes, color);
			StyleConstants.setForeground(attributes, color);
			document.insertString(document.getLength(), output + "\r\n", attributes);
		} catch (Throwable e) {
			// TODO: java.lang.Error: Interrupted attempt to acquire write lock
		}
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
		updateSlider(player);
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
	@Override public void onDecisionRequest(final Game game, final Player player)
	{
		updatePlayerTable();

		Color playerColor = colors.get(player);

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
	 * Called when the game has received a decision.
	 *
	 * @param game     The current game instance.
	 * @param player   The player whose decision was received.
	 * @param decision The decision that was made by the player.
	 */
	@Override public void onDecisionResponse(Game game, Player player, GameDecision decision)
	{
		disableDecisionButtons();
		removeDecisionButtonListeners();

		if (decision == GameDecision.CONTINUE) {
			writeToTextPane(String.format("%s continued with %d points.", player.getName(), player.getTurnPoints()), colors.get(player));
			return;
		}

		if (decision == GameDecision.SAVE) {
			writeToTextPane(String.format("%s saved %d points.", player.getName(), player.getTurnPoints()), colors.get(player));
			return;
		}

		if (decision == GameDecision.BET) {
			writeToTextPane(String.format("%s bet %s points.", player.getName(), player.getCurrentBet()), colors.get(player));
			return;
		}
	}

	private void handleBet(Game game, Player player)
	{
		try {
			int value = betSlider.getValue();
			System.out.println("handle bet" + value);
			game.respondBet(player, value);
		} catch (Exception e) {
			//TODO
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
		writeToTextPane(
				String.format(
						"%s was forced to reset their bank account losing %d points.",
						player.getName(),
						player.getTotalPoints()
				),
				colors.get(player)
		);
	}

	/**
	 * Called when a player must reset their turn total.
	 *
	 * @param game   The current game instance.
	 * @param player The player who must reset their turn total.
	 */
	@Override public void onSoftReset(Game game, Player player)
	{
		writeToTextPane(
				String.format(
						"%s lost their turn and %d points.",
						player.getName(),
						player.getTurnPoints()
				),
				colors.get(player)
		);
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
