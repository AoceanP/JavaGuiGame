/**
 * The NumGame interface defines the contract that all game classes must follow to
 * integrate with the JavaFX application lifecycle.
 * By implementing this interface, a game class guarantees that it provides a `start(Stage)`
 * method compatible with JavaFX’s application lifecycle. This allows the Main class, or any
 * shared game launcher, to treat different games interchangeably, as long as they conform to this interface.
 * The interface promotes consistency across game implementations (e.g., WordGame, NumberGame, MyGame),
 * making the project easier to manage and scale as more games are added.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public interface NumGame
{
    /**
     * Starts the game by initializing and displaying the JavaFX user interface.
     * This method is typically called by the JavaFX runtime when the game is launched, or it can
     * be manually invoked via the Main class. It is responsible for configuring the game’s UI,
     * initializing any necessary game logic, and displaying the primary stage for user interaction.
     * The `start` method must be implemented in each game class that conforms to this interface.
     *
     * @param stage the primary {@link javafx.stage.Stage} used to display the game’s user interface
     */
    void start(javafx.stage.Stage stage);
}
