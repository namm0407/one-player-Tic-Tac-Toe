package a4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A one-player Tic-Tac-Toe game where the human player ('X') competes against a computer opponent ('O')
 * 
 * @version 1.0 
 * @since   2025-11-08
 */
public class TicTacToe {
    // Game state
    private char[][] gameBoard = new char[3][3];
    private boolean playerTurn = false;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private String playerName = "";

    // Composition: Own a JFrame instead of extending it
    private JFrame window;

    // Scores
    private int playerWins = 0;
    private int computerWins = 0;
    private int draws = 0;

    // GUI components
    private JTextField nameField;
    private JButton submitButton;
    private JLabel messageLabel;
    private JButton[][] buttons = new JButton[3][3];
    private JLabel playerWinLabel;
    private JLabel computerWinLabel;
    private JLabel drawLabel;
    private JLabel timeLabel;
    private Timer timeTimer;
    private SimpleDateFormat timeFormat;

    /**
     * Constructs the Tic Tac Toe frame
     * Implementation of the functionality of Restart
     * Implementation of the functionality of Exit
     */
    public TicTacToe() {
        // window 
        window = new JFrame("Tic Tac Toe");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());

        // Current time
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeLabel = new JLabel("Current Time: " + timeFormat.format(new Date()));
        timeTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLabel.setText("Current Time: " + timeFormat.format(new Date()));
            }
        });
        timeTimer.start();

        // Menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu controlMenu = new JMenu("Control");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        controlMenu.add(exitItem); 

        JMenu helpMenu = new JMenu("Help");
        JMenuItem instructionItem = new JMenuItem("Instruction");
        instructionItem.addActionListener(e -> showInstructions());
        helpMenu.add(instructionItem);

        menuBar.add(controlMenu);
        menuBar.add(helpMenu);
        window.setJMenuBar(menuBar);

        // Message label
        messageLabel = new JLabel("Enter your player name...");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Board panel
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        ActionListener boardListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (e.getSource() == buttons[i][j]) {
                            makePlayerMove(i, j);
                        }
                    }
                }
            }
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 72)); 
                buttons[i][j].setPreferredSize(new Dimension(150, 150)); 
                buttons[i][j].addActionListener(boardListener);
                gameBoard[i][j] = ' ';
                boardPanel.add(buttons[i][j]);
            }
        }

        // Scores panel
        JPanel scoresPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        JLabel scoreHeader = new JLabel("Score");
        scoreHeader.setHorizontalAlignment(SwingConstants.LEFT);
        playerWinLabel = new JLabel("Player Wins: 0");
        playerWinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        computerWinLabel = new JLabel("Computer Wins: 0");
        computerWinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        drawLabel = new JLabel("Draws: 0");
        drawLabel.setHorizontalAlignment(SwingConstants.LEFT);
        scoresPanel.add(scoreHeader);
        scoresPanel.add(playerWinLabel);
        scoresPanel.add(computerWinLabel);
        scoresPanel.add(drawLabel);
        scoresPanel.setPreferredSize(new Dimension(150, 200)); // Added for breathing room

        // Game center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(boardPanel, BorderLayout.WEST);
        centerPanel.add(scoresPanel, BorderLayout.EAST);

        // Main game panel
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(messageLabel, BorderLayout.NORTH);
        gamePanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for name input and time
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); 

        JPanel namePanel = new JPanel(new FlowLayout()); 
        namePanel.add(new JLabel("Enter your name:"));
        nameField = new JTextField(10);
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitName());
        namePanel.add(nameField);
        namePanel.add(submitButton);

        bottomPanel.add(namePanel);
        bottomPanel.add(timeLabel); // On new line

        // Add panels to window (key fix!)
        window.add(gamePanel, BorderLayout.CENTER);
        window.add(bottomPanel, BorderLayout.SOUTH);

        window.pack();
        window.setSize(700, 600);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);  
    }

    /**
     * Implementation of restricting players to make their move before they submit their names
     * Implementation of restricting players to enter and submit their names more than ONCE
     * Implementation of updating the frame title after players submit their names
     * Implementation of correct message title after players submit their names
     */
    private void submitName() {
        String input = nameField.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        playerName = input;
        nameField.setEditable(false);
        submitButton.setEnabled(false);
        window.setTitle("Tic Tac Toe - Player: " + playerName);  
        messageLabel.setText("WELCOME " + playerName.toUpperCase());
        playerTurn = true;
        gameStarted = false;
    }

    /**
     * Processes the player's move at the specified position
     *
     * @param row the row index (0-2)
     * @param col the column index (0-2)
     */
    private void makePlayerMove(int row, int col) {
        if (gameOver || !playerTurn || playerName.isEmpty() || gameBoard[row][col] != ' ') {
            return; // Invalid move
        }

        gameBoard[row][col] = 'X';
        buttons[row][col].setText("X");
        buttons[row][col].setForeground(Color.GREEN);

        if (checkWin('X')) {
            playerWins++;
            updateScores();
            endGame("Player wins!");
            return;
        }

        if (isBoardFull()) {
            draws++;
            updateScores();
            endGame("It's a draw!");
            return;
        }

        playerTurn = false;
        gameStarted = true;
        updateMessage("Valid move, waiting for your opponent.");

        // Schedule computer move after 2 seconds
        new Timer(2000, e -> {
            makeComputerMove();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    /**
     * Makes a random move (computer)
     */
    private void makeComputerMove() {
        if (gameOver || playerTurn) {
            return;
        }

        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] == ' ') {
                    emptyCells.add(i * 3 + j);
                }
            }
        }

        //just in case
        if (emptyCells.isEmpty()) {
            return; 
        }

        int randomIndex = (int) (Math.random() * emptyCells.size());
        int cellIndex = emptyCells.get(randomIndex);
        int row = cellIndex / 3;
        int col = cellIndex % 3;

        gameBoard[row][col] = 'O';
        buttons[row][col].setText("O");
        buttons[row][col].setForeground(Color.RED);

        if (checkWin('O')) {
            computerWins++;
            updateScores();
            endGame("Computer wins!");
            return;
        }

        if (isBoardFull()) {
            draws++;
            updateScores();
            endGame("It's a draw!");
            return;
        }

        playerTurn = true;
        updateMessage("Your opponent has moved, now is your turn.");
    }

    /**
     * Implementation of the 3 conditions: Player wins, Computer wins and Draw
     * It checks if the player or computer has won the game
     *
     * @param mark the mark to check
     * @return true if the mark has three in a row, column, or diagonal
     */
    private boolean checkWin(char mark) {
        // Rows
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][0] == mark && gameBoard[i][1] == mark && gameBoard[i][2] == mark) {
                return true;
            }
        }
        // Columns
        for (int j = 0; j < 3; j++) {
            if (gameBoard[0][j] == mark && gameBoard[1][j] == mark && gameBoard[2][j] == mark) {
                return true;
            }
        }
        // Diagonals
        if (gameBoard[0][0] == mark && gameBoard[1][1] == mark && gameBoard[2][2] == mark) {
            return true;
        }
        if (gameBoard[0][2] == mark && gameBoard[1][1] == mark && gameBoard[2][0] == mark) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the board is full (draw)
     *
     * @return true if no empty cells remain
     */
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Updates the score labels
     */
    private void updateScores() {
        playerWinLabel.setText("Player Wins: " + playerWins);
        computerWinLabel.setText("Computer Wins: " + computerWins);
        drawLabel.setText("Draws: " + draws);
    }

    /**
     * change the message label
     *
     * @param message the new message text
     */
    private void updateMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Ends the game (disables button and shows the result dialog)
     * Implementation of the functionality of Restart
     *
     * @param result the result message
     */
    private void endGame(String result) {
        gameOver = true;
        disableButtons();
        Object[] options = {"Yes"};
        int choice = JOptionPane.showOptionDialog(window, result, "Game Over",  
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        }
    }

    /**
     * Disables all board buttons
     */
    private void disableButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    /**
     * Implementation of the functionality Restart
     */
    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameBoard[i][j] = ' ';
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
        playerTurn = true;
        gameOver = false;
        gameStarted = false;
        updateMessage("WELCOME " + playerName.toUpperCase());
    }

    /**
     * Shows the instructions
     * Implementation of the functionality of Help
     */
    private void showInstructions() {
        String instructions = "Some information about the game:\n" +
                "• The move is not occupied by any mark.\n" +
                "• The move is made in the player's turn.\n" +
                "• The move is made within the 3 x 3 board.\n" +
                "The game would continue and switch among the opposite player until it reaches either one of the following conditions:\n" +
                "• Player wins.\n" +
                "• Computer wins.\n" +
                "• Draw.\n\n";
        Object[] options = {"Yes"};
        JOptionPane.showOptionDialog(window, instructions, "Instructions",  
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    }

    /**
     * launch the application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TicTacToe();  
        });
    }
}
