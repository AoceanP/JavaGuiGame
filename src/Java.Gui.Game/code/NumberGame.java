import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Random;

/**
 * NumberGame is a JavaFX-based logic game where players must place
 * 20 randomly generated numbers into a 4×5 grid.
 * The numbers must be placed in a way that follows the following rules:
 * - Numbers placed to the left of a given number must be smaller.
 * - Numbers placed to the right must be larger.
 * The game ends when either:
 * - All 20 numbers are placed correctly (win).
 * - The player cannot place the next number due to no valid spaces (loss).
 * This class sets up the game, handles UI components, generates random numbers,
 * and manages the game state through the player’s interaction with the grid.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class NumberGame extends Application implements NumGame
{
    private static final int GRID_ROWS = 4;
    private static final int GRID_COLUMNS = 5;
    private static final int GRID_SIZE = GRID_ROWS * GRID_COLUMNS;
    private static final int EMPTY = -1;
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 1000;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 45;
    private static final int GAP = 4;
    private static final int ROOT_SPACING = 5;
    private static final int ROOT_PADDING = 10;
    private static final int DIALOG_PADDING = 20;
    private static final int DIALOG_SPACING = 15;
    private static final String EMPTY_SLOT_TEXT = "[ ]";
    private static final String STYLE_PATH = "style.css";

    private Label numberLabel;
    Button[] buttons;
    int[] placedNumbers;
    private int currentCount;
    int currentNumber;
    private Stage primaryStage;
    private final Random random = new Random();

    /**
     * AbstractGame instance for tracking player performance, such as games played and games won.
     * This is an anonymous subclass that overrides the printStats method to show game statistics.
     */
    private final AbstractGame stats = new AbstractGame()
    {
        @Override
        public void printStats()
        {
            super.printStats(); // Print general game statistics like games played
        }
    };

    /**
     * Initializes the main game window, setting up the grid layout, buttons, and the game UI.
     * This method also shows the welcome dialog once the UI is ready.
     *
     * @param stage The primary JavaFX stage where the game UI will be displayed
     */
    @Override
    public void start(final Stage stage)
    {
        this.primaryStage = stage;

        numberLabel = new Label("Welcome to the Number Game!");
        numberLabel.setId("top-label");

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(GAP);
        gridPane.setVgap(GAP);
        gridPane.setAlignment(Pos.CENTER);

        buttons = new Button[GRID_SIZE];
        placedNumbers = new int[GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++)
        {
            final int index = i;
            final Button button = new Button(EMPTY_SLOT_TEXT);
            button.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
            button.setOnAction(e -> handleClick(index));
            buttons[i] = button;
            placedNumbers[i] = EMPTY;
            gridPane.add(button, i % GRID_COLUMNS, i / GRID_COLUMNS);
        }

        final VBox root = new VBox(ROOT_SPACING, numberLabel, gridPane);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(ROOT_PADDING));

        final Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(STYLE_PATH);

        stage.setTitle("Number Game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Platform.runLater(this::showWelcomeMessage);
    }

    /**
     * Displays a dialog with game instructions and a "Start" button.
     * The player can start the game by clicking the "Start" button.
     */
    private void showWelcomeMessage()
    {
        final Stage dialog = new Stage();
        dialog.setTitle("Welcome");
        dialog.initOwner(primaryStage);

        final Label message = new Label("Welcome to the 20-Number Challenge! Click 'Start' to begin.");
        message.setWrapText(true);
        message.setId("welcome-message");

        final Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            dialog.close();
            resetGame();
        });

        final VBox layout = new VBox(DIALOG_SPACING, message, startButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(DIALOG_PADDING));
        layout.getStyleClass().add("welcome-dialog-box");

        final Scene scene = new Scene(layout);
        scene.getStylesheets().add(STYLE_PATH);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Handles the click event when a player clicks a grid cell.
     * This method places the current number into the selected grid cell if the placement is valid.
     * If the grid is full, it triggers the win message.
     * If the move is valid, it generates the next random number for placement.
     *
     * @param index the index of the clicked button in the grid
     */
    private void handleClick(final int index)
    {
        if (index < 0 || index >= GRID_SIZE || placedNumbers[index] != EMPTY || !isValidPlacement(index, currentNumber))
        {
            return;
        }

        placedNumbers[index] = currentNumber;
        buttons[index].setText(String.valueOf(currentNumber));
        currentCount++;

        if (currentCount == GRID_SIZE)
        {
            showWinMessage();
        }
        else
        {
            generateNextNumber();
        }
    }

    /**
     * Generates the next random number to be placed in the grid.
     * The number is between MIN_NUMBER and MAX_NUMBER.
     * If no valid placements remain for the generated number, the game ends in a loss.
     */
    private void generateNextNumber()
    {
        currentNumber = random.nextInt(MAX_NUMBER) + MIN_NUMBER;

        if (!hasValidMove())
        {
            showLossMessage();
        }
        else
        {
            numberLabel.setText("Next number: " + currentNumber + " - Select a slot.");
        }
    }

    /**
     * Checks if there is at least one valid move available for the current number.
     * A valid move means there is an empty cell where the current number can be placed.
     *
     * @return true if there is a valid move, false otherwise
     */
    boolean hasValidMove()
    {
        for (int i = 0; i < GRID_SIZE; i++)
        {
            if (placedNumbers[i] == EMPTY && isValidPlacement(i, currentNumber))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the placement rule: numbers to the left must be smaller,
     * and numbers to the right must be larger.
     *
     * @param index  the target index in the grid
     * @param number the number to test for validity
     * @return true if the placement is valid, false otherwise
     */
    boolean isValidPlacement(final int index, final int number)
    {
        if (placedNumbers[index] != EMPTY)
        {
            return false;
        }

        for (int i = 0; i < index; i++)
        {
            if (placedNumbers[i] != EMPTY && placedNumbers[i] > number)
            {
                return false;
            }
        }

        for (int i = index + 1; i < GRID_SIZE; i++)
        {
            if (placedNumbers[i] != EMPTY && placedNumbers[i] < number)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Resets the game by clearing the grid and starting a new round.
     * It resets counters, grid cells, and generates the first number for the new game.
     */
    private void resetGame()
    {
        currentCount = 0;

        for (int i = 0; i < GRID_SIZE; i++)
        {
            placedNumbers[i] = EMPTY;
            buttons[i].setText(EMPTY_SLOT_TEXT);
        }

        generateNextNumber();
    }

    /**
     * Displays a loss message when the player cannot place the next number.
     * Allows the player to either restart or quit the game.
     */
    private void showLossMessage()
    {
        stats.incrementGamesPlayed();
        stats.addPlacements(currentCount);

        numberLabel.setText("Impossible to place the next number: " + currentNumber);

        final Stage dialog = new Stage();
        dialog.setTitle("Game Over");
        dialog.initOwner(primaryStage);

        final Label message = new Label("Game Over! Impossible to place the next number: " + currentNumber + ". Try again?");
        message.setWrapText(true);

        final Button tryAgain = new Button("Try Again");
        final Button quit = new Button("Quit");

        tryAgain.setOnAction(e -> {
            dialog.close();
            resetGame();
        });

        quit.setOnAction(e -> {
            dialog.close();
            stats.printStats();
            Platform.exit();
        });

        final HBox buttonRow = new HBox(DIALOG_SPACING, tryAgain, quit);
        buttonRow.setAlignment(Pos.CENTER);

        final VBox layout = new VBox(DIALOG_SPACING, message, buttonRow);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(DIALOG_PADDING));
        layout.getStyleClass().add("game-over-dialog");

        final Scene scene = new Scene(layout);
        scene.getStylesheets().add(STYLE_PATH);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Displays a win message when the player successfully completes the game.
     * The player can then choose to restart or quit the game.
     */
    private void showWinMessage()
    {
        stats.incrementGamesPlayed();
        stats.incrementGamesWon();
        stats.addPlacements(GRID_SIZE);

        final Stage dialog = new Stage();
        dialog.setTitle("Victory!");
        dialog.initOwner(primaryStage);

        final Label message = new Label("You win! Great job! Would you like to play again?");
        message.setWrapText(true);

        final Button tryAgain = new Button("Try Again");
        final Button quit = new Button("Quit");

        tryAgain.setOnAction(e -> {
            dialog.close();
            resetGame();
        });

        quit.setOnAction(e -> {
            dialog.close();
            stats.printStats();
            Platform.exit();
        });

        final HBox buttonRow = new HBox(DIALOG_SPACING, tryAgain, quit);
        buttonRow.setAlignment(Pos.CENTER);

        final VBox layout = new VBox(DIALOG_SPACING, message, buttonRow);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(DIALOG_PADDING));
        layout.getStyleClass().add("game-over-dialog");

        final Scene scene = new Scene(layout);
        scene.getStylesheets().add(STYLE_PATH);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}