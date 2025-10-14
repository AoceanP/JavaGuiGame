import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 * MyGame is the launcher class for the Blade Rush JavaFX game.
 * This game starts at the home base scene and allows the player to upgrade stats,
 * purchase potions, and enter the tower to fight enemies in wave-based levels.
 * Responsibilities:
 * - Initialize the JavaFX application (entry point)
 * - Start and maintain background music
 * - Launch the home scene and tower level scene as needed
 * Dependencies:
 * - HomeBaseUI (for UI and navigation)
 * - TowerLevel (core game logic and wave handling)
 * - mygamestyle.css (for consistent UI styling)
 * - gamemusic.mp3 (in /resources/)
 * All resources must exist or scenes may fallback to default styling or silent play.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class MyGame extends Application
{

    /**
     * The global Player instance used across all scenes.
     * - Maintains stat and inventory state throughout the session.
     */
    public static final Player player = new Player();

    /**
     * The media player instance used for playing background music.
     * - Loops indefinitely at 20% volume.
     */
    private static MediaPlayer musicPlayer;

    // === Constants ===
    private static final double MUSIC_VOLUME = 0.2;
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;

    /**
     * JavaFX lifecycle method that starts the application.
     * - Sets the initial scene to the home base
     * - Begins background music playback
     * - Configures the window title
     *
     * @param primaryStage the main JavaFX stage window
     */
    @Override
    public void start(final Stage primaryStage)
    {
        playBackgroundMusic();

        final Scene homeScene = HomeBaseUI.createHomeScene(primaryStage, player);
        primaryStage.setTitle("Blade Rush");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    /**
     * Starts playing background music in a loop at 20% volume.
     * The MP3 file must exist at /resources/gamemusic.mp3 in the classpath.
     * If the file is missing or fails to load, music will be skipped silently.
     */
    private void playBackgroundMusic()
    {
        try
        {
            final Media bgMusic = new Media(getClass()
                    .getResource("/resources/gamemusic.mp3")
                    .toExternalForm());
            musicPlayer = new MediaPlayer(bgMusic);
            musicPlayer.setVolume(MUSIC_VOLUME);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.play();
        }
            catch (Exception e)
        {
            // Music file missing or invalid — skipping background music
        }
    }

    /**
     * Transitions the player into the tower level scene.
     * Initializes the TowerLevel class with required data and sets up the JavaFX scene.
     * Loads the custom CSS styling from mygamestyle.css if present.
     *
     * @param stage            the JavaFX window used for displaying the game
     * @param player           the current player state object
     * @param potionsAvailable how many potions the player can use during this tower run
     */
    public static void startTowerScene(final Stage stage, final Player player, final int potionsAvailable) {
        final Pane gamePane = new Pane();
        gamePane.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);

        final TowerLevel towerLevel = new TowerLevel(gamePane, stage, player, potionsAvailable);
        towerLevel.startCurrentLevel();

        final Scene towerScene = new Scene(gamePane);

        try
        {
            towerScene.getStylesheets().add("mygamestyle.css");
        }
            catch (Exception e)
        {
            // Stylesheet file not found — using default JavaFX styling
        }

        stage.setTitle("Blade Rush - Tower");
        stage.setScene(towerScene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args the command-line arguments passed to Java (unused)
     */
    public static void main(final String[] args)
    {
        launch(args);
    }
}