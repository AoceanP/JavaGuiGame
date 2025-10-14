import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The ShopUI class displays the potion shop interface where players can purchase healing potions.
 * - The shop allows a player to buy up to 10 potions per game session.
 * - The user interface is responsible for displaying the potion details, the number of available potions,
 *   and buttons to buy the potions or return to the home base.
 * This class also manages the logic for enabling and resetting the shop, allowing the player to buy potions again
 * after certain milestones (e.g., after defeating a boss).
 * The implementation complies with the coding standards outlined for the COMP 2522 course and adheres to Java code formatting conventions.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public final class ShopUI {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int MAX_POTIONS = 10;
    private static int potionsBought = 0;
    private static boolean shopResetEnabled = false;

    /**
     * Creates and returns the shop scene where players can purchase potions.
     * - Sets up the UI elements such as the background, labels, and buttons.
     * - The "Buy Potion" button updates the number of potions purchased and disables itself once the maximum number of potions is reached.
     * - The player can return to the home base by clicking the "Return to Home" button.
     *
     * @param stage  - The primary JavaFX stage where the shop scene is displayed.
     * @param player - The current player interacting with the shop.
     * @return the JavaFX Scene representing the potion shop interface.
     */
    public static Scene createShopScene(final Stage stage, final Player player)
    {
        final Image bgImage = new Image("dungeon.png"); // - Load the background image for the shop
        final ImageView background = new ImageView(bgImage);
        background.setFitWidth(WIDTH); // - Set the background image width
        background.setFitHeight(HEIGHT); // - Set the background image height
        background.setPreserveRatio(false); // - Ensure the background fills the window

        // - Create and style the title label
        Label title = new Label("Potion Shop");
        title.getStyleClass().add("shop-title");

        // - Create and style the informational label
        Label info = new Label("You can only buy up to 10 potions per run.");
        info.getStyleClass().add("shop-info");

        // - Set up the potion icon and label
        ImageView potionIcon = new ImageView(new Image("potion.png"));
        potionIcon.setFitWidth(40);
        potionIcon.setFitHeight(40);

        Label potionLabel = new Label("Healing Potion");
        potionLabel.getStyleClass().add("shop-label");

        // - Label displaying the number of remaining potions to buy
        Label potionsLeftLabel = new Label("Potions left: " + (MAX_POTIONS - potionsBought));
        potionsLeftLabel.getStyleClass().add("shop-label");

        // - Button to buy potions, increments the potion count
        Button buyPotionButton = new Button("Buy Potion");
        buyPotionButton.setOnAction(e -> {
            if (potionsBought < MAX_POTIONS)
            {
                potionsBought++; // - Increment the number of potions bought
                potionsLeftLabel.setText("Potions left: " + (MAX_POTIONS - potionsBought)); // - Update the label
                System.out.println("Potion bought: " + potionsBought); // - Print confirmation to the console
            }

            // - Disable the button once the player has reached the maximum potion limit
            if (potionsBought >= MAX_POTIONS)
            {
                buyPotionButton.setDisable(true);
            }
        });

        // - Disable the "Buy Potion" button if the limit has already been reached
        if (potionsBought >= MAX_POTIONS) {
            buyPotionButton.setDisable(true);
        }

        // - Button to return to the home base scene
        Button backButton = new Button("Return to Home");
        backButton.setOnAction(e -> stage.setScene(HomeBaseUI.createHomeScene(stage, player)));

        // - Layout the elements in a vertical box
        VBox layout = new VBox(10, title, info, potionIcon, potionLabel, potionsLeftLabel, buyPotionButton, backButton);
        layout.setAlignment(Pos.CENTER); // - Center align the layout
        layout.setPadding(new Insets(20)); // - Add padding around the layout
        layout.getStyleClass().add("shop-pane");

        // - Combine the background and layout elements in a StackPane
        StackPane root = new StackPane(background, layout);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // - Attempt to load the custom CSS file for styling; handle any errors that occur during loading
        try {
            scene.getStylesheets().add("mygamestyle.css");
        } catch (Exception e) {
            System.err.println("⚠ Failed to load CSS file.");
        }

        return scene; // - Return the created scene to be displayed
    }

    /**
     * Retrieves the number of potions the player has bought.
     * - This method provides access to the `potionsBought` variable, which tracks the player's potion purchases.
     *
     * @return the total number of potions the player has purchased.
     */
    public static int getPotionsBought()
    {
        return potionsBought; // - Return the number of potions bought by the player
    }

    /**
     * Enables the option to reset the shop, allowing the player to buy potions again.
     * - This method should be called after certain game events (e.g., after defeating a boss),
     *   enabling the player to buy potions once more.
     */
    public static void enableShopReset()
    {
        shopResetEnabled = true; // - Set the flag to allow the shop reset
    }

    /**
     * Resets the potion counter if resetting is enabled.
     * - This method allows the player to purchase potions again, typically after completing
     *   a significant game milestone (e.g., defeating a boss).
     * - If the shop reset is not enabled, this method will not perform any action.
     */
    public static void resetShop()
    {
        if (shopResetEnabled)
        {
            potionsBought = 0; // - Reset the number of potions bought
            shopResetEnabled = false; // - Disable further shop resets until explicitly enabled again
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     * - This constructor is private because the ShopUI class is meant to be used statically and should not be instantiated.
     */
    private ShopUI()
    {
        // - Prevent instantiation
    }
}