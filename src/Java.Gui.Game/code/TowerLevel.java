import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Manages a wave-based battle level in the Blade Rush game.
 * This class handles all gameplay logic related to the tower combat scene.
 * Core Responsibilities:
 * - Spawn appropriate tower enemies per level.
 * - Manage transitions between levels and the home base.
 * - Display player HP and potion count through a live UI.
 * - Enable potion usage and feedback effects.
 * - Load and style popup dialogues with custom CSS.
 * Game Flow:
 * - Levels progress from regular tower fights (Level 1 & 2) to a boss encounter (Level 3).
 * - The player can heal using potions if available and not at max HP.
 * - Upon clearing all enemies, a level completion popup is shown.
 * - If the player dies, a Game Over popup provides the option to restart from the home base.
 * Styling:
 * - Dialogues and UI use `/mygamestyle.css` for a consistent game aesthetic.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class TowerLevel {

    // Constants
    private static final int SLIME_X = 100;
    private static final int SLIME_Y = 300;
    private static final int SLIME_WIDTH = 150;
    private static final int SLIME_HEIGHT = 150;

    private static final int TOWER_X = 500;
    private static final int TOWER_Y = 200;

    private static final int LABEL_X = 20;
    private static final int LABEL_Y = 20;
    private static final int LABEL_FONT_SIZE = 18;

    private static final int POTION_LABEL_X = 130;
    private static final int POTION_LABEL_Y = 65;
    private static final int POTION_FONT_SIZE = 14;

    private static final int BUTTON_X = 20;
    private static final int BUTTON_Y = 60;
    private static final int HEAL_AMOUNT = 20;

    private static final int FEEDBACK_X = 300;
    private static final int FEEDBACK_Y = 100;
    private static final double FEEDBACK_DURATION_SECONDS = 2.0;

    private static final int BG_WIDTH = 800;
    private static final int BG_HEIGHT = 600;

    private static final int DIALOG_WIDTH = 450;
    private static final int DIALOG_HEIGHT = 220;
    private static final int GAME_OVER_WIDTH = 112;
    private static final int GAME_OVER_HEIGHT = 55;
    private static final int CURRENT_LEVEL = 1;
    private static final int CURRENT_LEVEL2 = 2;

    private final Pane gamePane;
    private final Stage stage;
    private final Player player;
    private final List<TowerEnemy> activeTowers = new ArrayList<>();

    private int currentLevel = 1;
    private Label hpLabel;
    private Label potionLabel;
    private int potionsAvailable;
    private Button usePotionButton;

    /**
     * Constructs a new TowerLevel object to manage tower combat gameplay.
     * - Prepares core references for UI and player control.
     *
     * @param gamePane the root pane for rendering all elements
     * @param stage the main JavaFX window stage
     * @param player the player object involved in combat
     * @param potionsAvailable initial number of healing potions
     */
    public TowerLevel(final Pane gamePane,
                      final Stage stage,
                      final Player player,
                      final int potionsAvailable) {
        this.gamePane = gamePane;
        this.stage = stage;
        this.player = player;
        this.potionsAvailable = potionsAvailable;
    }

    /**
     * Starts the current tower level and sets up all in-game visuals and enemies.
     * - Resets the background and clears previous towers.
     * - Heals the player to full HP before combat begins.
     * - Spawns a regular or boss tower based on the current level.
     * - Adds the slime image as the player avatar.
     * - Displays the healing UI components.
     * - If the player is already dead, the game ends immediately.
     */
    public void startCurrentLevel() {
        if (!player.isAlive()) {
            showGameOverPopup();
            return;
        }

        activeTowers.clear();
        reloadBackground();
        player.setCurrentHp(player.getMaxHp());

        if (currentLevel == CURRENT_LEVEL || currentLevel == CURRENT_LEVEL2)
        {
            spawnTower(TOWER_X, TOWER_Y, false);
        }
            else
        {
            spawnTower(TOWER_X, TOWER_Y, true);
        }

        try {
            final Image slimeImg = new Image(
                    Objects.requireNonNull(getClass().getResource("/resources/slime_with_bow.png")).toExternalForm()
            );
            final ImageView slimeView = new ImageView(slimeImg);
            slimeView.setX(SLIME_X);
            slimeView.setY(SLIME_Y);
            slimeView.setFitWidth(SLIME_WIDTH);
            slimeView.setFitHeight(SLIME_HEIGHT);
            gamePane.getChildren().add(slimeView);
        } catch (Exception e) {
            System.out.println("Failed to load slime image: " + e.getMessage());
        }

        setupHealingUI();
    }

    /**
     * Reloads the background image for the tower level scene.
     * - Loads `dungeon.png` from the resources folder.
     * - Sets the image to cover the full screen (800×600 px).
     * - Adds the background as the first child in the pane (drawn behind everything else).
     */
    private void reloadBackground()
    {
        try
        {
            final ImageView bg = new ImageView(
                    new Image(MyGame.class.getResource("/dungeon.png").toExternalForm())
            );
            bg.setFitWidth(BG_WIDTH);
            bg.setFitHeight(BG_HEIGHT);
            bg.setPreserveRatio(false);
            gamePane.getChildren().add(0, bg);
        }
            catch (Exception e)
        {
            System.out.println("Failed to load background: " + e.getMessage());
        }
    }

    /**
     * Spawns a tower enemy at the specified screen coordinates.
     * - Configures callbacks for checking wave completion and game over.
     *
     * @param x the X position of the enemy in pixels
     * @param y the Y position of the enemy in pixels
     * @param isBoss true if this enemy is the final boss, false otherwise
     */
    private void spawnTower(final double x, final double y, final boolean isBoss)
    {
        final TowerEnemy tower = new TowerEnemy(
                x, y, isBoss,
                this::checkLevelCleared,
                this::showGameOverPopup,
                gamePane, player, currentLevel
        );
        activeTowers.add(tower);
    }

    /**
     * Initializes the in-game healing interface.
     * - Displays player HP as a label (e.g., "HP: 100").
     * - Displays potion count with "xN" format.
     * - Adds a "Use Potion" button that:
     *   - Heals the player for 20 HP.
     *   - Decreases potion count by one.
     *   - Updates both labels.
     *   - Shows a floating feedback message.
     *
     * Behavior:
     * - Button only works if the player is alive and not at full HP.
     * - If potions are at 0 or HP is full, healing is blocked.
     */
    private void setupHealingUI()
    {
        hpLabel = new Label("HP: " + player.getCurrentHp());
        hpLabel.setLayoutX(LABEL_X);
        hpLabel.setLayoutY(LABEL_Y);
        hpLabel.setFont(Font.font(LABEL_FONT_SIZE));
        hpLabel.getStyleClass().add("popup-stat");

        potionLabel = new Label("x" + potionsAvailable);
        potionLabel.setLayoutX(POTION_LABEL_X);
        potionLabel.setLayoutY(POTION_LABEL_Y);
        potionLabel.setFont(Font.font(POTION_FONT_SIZE));
        potionLabel.getStyleClass().add("popup-stat");

        usePotionButton = new Button("Use Potion");
        usePotionButton.setLayoutX(BUTTON_X);
        usePotionButton.setLayoutY(BUTTON_Y);
        usePotionButton.getStyleClass().add("popup-button");

        usePotionButton.setOnAction(e ->
        {
            if (!player.isAlive()) return;

            final boolean canHeal = potionsAvailable > 0 && player.getCurrentHp() < player.getMaxHp();

            if (canHeal) {
                player.heal(HEAL_AMOUNT);
                potionsAvailable--;
                hpLabel.setText("HP: " + player.getCurrentHp());
                potionLabel.setText("x" + potionsAvailable);
                showHealPopup("Healed 20 HP. Potions left: " + potionsAvailable);
            } else {
                showHealPopup("Cannot heal. No potions or full HP.");
            }
        });

        gamePane.getChildren().addAll(hpLabel, usePotionButton, potionLabel);
    }

    /**
     * Displays floating healing feedback text in the center-top of the screen.
     *
     * - Used after a successful or failed potion usage attempt.
     *
     * @param message the text to show (e.g., "Healed 20 HP", "No potions left")
     */
    private void showHealPopup(final String message) {
        final Text feedback = new Text(message);
        feedback.setStyle("-fx-font-size: 16px; -fx-fill: white;");
        feedback.setLayoutX(FEEDBACK_X);
        feedback.setLayoutY(FEEDBACK_Y);
        gamePane.getChildren().add(feedback);

        final FadeTransition ft = new FadeTransition(Duration.seconds(FEEDBACK_DURATION_SECONDS), feedback);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> gamePane.getChildren().remove(feedback));
        ft.play();
    }

    /**
     * Checks whether all enemies in the level are defeated.
     * - If true, shows the "Level Complete" confirmation popup.
     */
    private void checkLevelCleared()
    {
        final boolean allDefeated = activeTowers.stream().noneMatch(TowerEnemy::isAlive);
        if (allDefeated)
        {
            showLevelCompletePopup();
        }
    }

    /**
     * Displays a popup allowing the player to either continue or return home.
     * - Called after all towers in a level are defeated.
     * - Loads and applies custom CSS from `mygamestyle.css`.
     */
    private void showLevelCompletePopup()
    {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Level Complete");
        alert.setHeaderText("You cleared the level!");
        alert.setContentText("What would you like to do next?");

        final ButtonType next = new ButtonType("Next");
        final ButtonType home = new ButtonType("Return Home");
        alert.getButtonTypes().setAll(next, home);

        final DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(DIALOG_WIDTH);
        dialogPane.setMinHeight(DIALOG_HEIGHT);

        try
        {
            dialogPane.getStylesheets().add(
                    getClass().getResource("/mygamestyle.css").toExternalForm()
            );
        }
            catch (Exception e)
        {
            System.out.println("Failed to load CSS for Level Complete popup: " + e.getMessage());
        }

        final Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(choice ->
        {
            if (choice.equals(next))
            {
                currentLevel++;
                startCurrentLevel();
                MyGame.startTowerScene(stage, player, potionsAvailable);
            }
                else
            {
                stage.setScene(HomeBaseUI.createHomeScene(stage, player));
            }
        });
    }

    /**
     * Displays a Game Over popup when the player dies.
     * - Allows the player to return to the home base.
     * - Loads custom CSS for styling.
     */
    private void showGameOverPopup()
    {
        Platform.runLater(() ->
        {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("You died!");
            alert.setContentText("Would you like to play again?");

            final ButtonType retry = new ButtonType("Return to Town");
            alert.getButtonTypes().setAll(retry);

            final DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setMinWidth(GAME_OVER_WIDTH);
            dialogPane.setMinHeight(GAME_OVER_HEIGHT);

            try
            {
                dialogPane.getStylesheets().add(
                        getClass().getResource("/mygamestyle.css").toExternalForm()
                );
            }
                catch (Exception e)
            {
                System.out.println("Failed to load CSS for Game Over popup: " + e.getMessage());
            }

            final Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(choice ->
            {
                if (choice.equals(retry))
                {
                    player.reset();
                    stage.setScene(HomeBaseUI.createHomeScene(stage, player));
                }
            });
        });
    }
}