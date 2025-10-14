import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * HomeBaseUI is a utility class responsible for rendering the game's home base scene.
 * This is the central hub the player returns to after completing tower floors.
 * It provides essential UI options to:
 * - Buy healing potions before combat
 * - Enter the tower to start or continue progression
 * This class enforces a static-only design:
 * - Cannot be instantiated (private constructor)
 * - All public methods are static
 * Assumes:
 * - "dungeon.png" exists in the working directory
 * - "mygamestyle.css" is available and correctly linked to style buttons
 * Used by:
 * - Game entry point (e.g., after login/start)
 * - Tower level completion screen, when player chooses to return to base
 * Depends on:
 * - ShopUI (for shop scene)
 * - MyGame (for launching tower logic)
 * - Player (for state sharing)
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public final class HomeBaseUI
{
    /**
     * The fixed width of the game scene, matching the standard window size.
     * - Used to size background and layout container appropriately.
     */
    private static final int WIDTH = 800;

    /**
     * The fixed height of the game scene, matching the standard window size.
     * - Ensures the background image and layout fill the scene properly.
     */
    private static final int HEIGHT = 600;

    /**
     * The vertical spacing between buttons in the VBox layout.
     * - Provides consistent visual breathing room between UI options.
     */
    private static final int SPACING = 20;

    /**
     * Builds and returns the main home base screen.
     *
     * This scene includes:
     * - A background image (dungeon-style backdrop)
     * - Two primary buttons:
     *   - "Visit Potion Shop": sends player to ShopUI for healing items
     *   - "Enter Tower": launches tower combat using current potion count
     * Stylesheet:
     * - Attempts to load "mygamestyle.css" to apply consistent visual style
     * - If unavailable, default styles are used (no crash)
     * This method is called any time the player is meant to "return home."
     *
     * @param stage  the primary game window used for navigation
     * @param player the current player object holding stats and inventory
     * @return the fully constructed Scene representing the home base UI
     */
    public static Scene createHomeScene(final Stage stage, final Player player)
    {
        final Image bgImage = new Image("dungeon.png");
        final ImageView background = new ImageView(bgImage);
        background.setFitWidth(WIDTH);
        background.setFitHeight(HEIGHT);
        background.setPreserveRatio(false);

        final Button shopButton = new Button("Visit Potion Shop");
        final Button towerButton = new Button("Enter Tower");

        // - Opens the potion shop scene
        shopButton.setOnAction(e -> stage.setScene(ShopUI.createShopScene(stage, player)));

        // - Enters the tower with the current potion count
        towerButton.setOnAction(e -> {
            final int potions = ShopUI.getPotionsBought();
            MyGame.startTowerScene(stage, player, potions);
        });

        shopButton.getStyleClass().add("popup-button");
        towerButton.getStyleClass().add("popup-button");

        final VBox layout = new VBox(SPACING, shopButton, towerButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        final StackPane root = new StackPane(background, layout);
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        try
        {
            scene.getStylesheets().add("mygamestyle.css");
        }
        catch (Exception e)
        {
            // - Stylesheet not found; fallback to JavaFX default styling
            // - Avoid crashing the game over missing CSS
        }

        return scene;
    }

    /**
     * Private constructor to prevent instantiation.
     * This class is meant to be used statically only.
     * Instantiating this would serve no purpose and violates design intent.
     */
    private HomeBaseUI()
    {
        // Do not instantiate
    }
}