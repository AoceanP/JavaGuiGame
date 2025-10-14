import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Represents a tower enemy that interacts with the player in the game.
 * - Each tower can be a regular enemy or a boss depending on the level and flags passed in.
 * - Enemies attack the player on a repeating timer, dealing damage that scales with game level.
 * - The player can click the tower to deal damage, which decreases its HP.
 * - When HP reaches zero, the tower is removed and a callback is triggered.
 * - A health bar is shown above the tower to visually represent its current HP.
 * - Another callback is triggered if the enemy defeats the player.
 * Damage Scaling by Level:
 * - Level 1 = 5 damage per attack
 * - Level 2 = 10 damage per attack
 * - Level 3 = 15 damage per attack
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class TowerEnemy {

    private static final int REGULAR_HEALTH_HP = 300;
    private static final int MID_BOSS_HEALTH_HP = 450;
    private static final int BOSS_HEALTH_HP = 900;
    private static final int DAMAGE_REGULAR = 5;
    private static final int DAMAGE_MID_BOSS = 10;
    private static final int DAMAGE_BOSS = 15;
    private static final int IMAGE_WIDTH_PX = 120;
    private static final int HEALTH_BAR_HEIGHT_PX = 8;
    private static final int HEALTH_BAR_OFFSET_Y = 12;
    private static final int PLAYER_BASE_DAMAGE = 5;
    private static final int PLAYER_DAMAGE_MULTIPLIER = 2;
    private static final int LEVEL_ONE = 1;
    private static final int LEVEL_TWO = 2;
    private static final int LEVEL_THREE = 3;
    private static final double HEALTH_HIGH_THRESHOLD = 0.5;
    private static final double HEALTH_MEDIUM_THRESHOLD = 0.25;
    private static final int ATTACK_INTERVAL_SEC = 2;
    private static final int MIN_HP = 0;

    private final ImageView view;
    private final Rectangle healthBar;
    private final int maxHp;
    private int currentHp;
    private final boolean isBoss;
    private final Runnable onDeath;
    private final Runnable onPlayerDeath;
    private final Pane root;
    private final Player playerRef;
    private final int currentLevel;
    private boolean isAlive;
    private Timeline attackTimer;
    int damage;

    /**
     * Constructs a new TowerEnemy.
     * - Sets up the tower image and health bar and places them at the given screen coordinates.
     * - Initializes max HP based on whether the tower is a boss or mid-boss.
     * - Registers mouse click events so the tower can take damage from the player.
     * - Starts the attack timer so the enemy can damage the player repeatedly.
     *
     * @param x             the X position on screen where the tower will appear
     * @param y             the Y position on screen where the tower will appear
     * @param isBoss        true if this enemy is a boss (used for HP calculation)
     * @param onDeath       callback triggered when this enemy is defeated
     * @param onPlayerDeath callback triggered when the player dies
     * @param root          the Pane where the tower's visuals are added
     * @param player        reference to the Player object, used for dealing and receiving damage
     * @param currentLevel  the level of the game, used to determine attack strength
     */
    public TowerEnemy(final double x, final double y, final boolean isBoss,
                      final Runnable onDeath, final Runnable onPlayerDeath,
                      final Pane root, final Player player, final int currentLevel)
    {
        this.damage = damage;

        this.view = new ImageView(new Image("tower_castle.png"));
        this.view.setLayoutX(x);
        this.view.setLayoutY(y);
        this.view.setFitWidth(IMAGE_WIDTH_PX);
        this.view.setPreserveRatio(true);

        this.healthBar = new Rectangle(IMAGE_WIDTH_PX, HEALTH_BAR_HEIGHT_PX, Color.LIMEGREEN);
        this.healthBar.setLayoutX(x);
        this.healthBar.setLayoutY(y - HEALTH_BAR_OFFSET_Y);

        this.isBoss = isBoss;
        this.currentLevel = currentLevel;

        if (!isBoss && currentLevel == 2)
        {
            this.maxHp = MID_BOSS_HEALTH_HP;
        }
            else if (isBoss)
        {
            this.maxHp = BOSS_HEALTH_HP;
        }
            else
        {
            this.maxHp = REGULAR_HEALTH_HP;
        }

        this.currentHp = this.maxHp;
        this.onDeath = onDeath;
        this.onPlayerDeath = onPlayerDeath;
        this.root = root;
        this.playerRef = player;
        this.isAlive = true;

        this.root.getChildren().addAll(this.view, this.healthBar);
        this.view.setOnMouseClicked(this::handleClick);

        startAttackTimer();
    }

    /**
     * Handles when the tower is clicked by the player.
     * - Calculates the amount of damage the player will deal using:
     *   - A flat base value (PLAYER_BASE_DAMAGE)
     *   - A bonus based on the player's strength stat (PLAYER_DAMAGE_MULTIPLIER * strength)
     * - Passes the calculated damage to the enemy via takeDamage()
     *
     * @param event the mouse click event triggered by JavaFX
     */
    private void handleClick(final MouseEvent event)
    {
        if (this.isAlive)
        {
            final int damage = PLAYER_BASE_DAMAGE + (this.playerRef.getStrength() * PLAYER_DAMAGE_MULTIPLIER);
            takeDamage(damage);
        }
    }

    /**
     * Applies damage to the tower's current HP and updates its health bar.
     * - Subtracts the given damage from the tower's HP.
     * - If HP drops to zero or below:
     *   - Stops the attack loop.
     *   - Removes the tower and its health bar from the game pane.
     *   - Triggers the onDeath callback to notify the game manager.
     *
     * @param damage the amount of HP to subtract
     */
    public void takeDamage(final int damage)
    {
        this.currentHp -= damage;
        if (this.currentHp <  MIN_HP)
        {
            this.currentHp =  MIN_HP;
        }

        updateHealthBar();

        if (this.currentHp <=  MIN_HP && this.isAlive)
        {
            this.isAlive = false;
            stopAttacking();
            this.root.getChildren().removeAll(this.view, this.healthBar);
            this.onDeath.run();
        }
    }

    /**
     * Updates the visual health bar of the enemy based on current HP.
     * - The width of the bar is scaled proportionally to the current HP ratio.
     * - The bar color reflects current health state:
     *   - Green if health is above HEALTH_HIGH_THRESHOLD.
     *   - Orange if health is above HEALTH_MEDIUM_THRESHOLD but below high.
     *   - Red if health is below HEALTH_MEDIUM_THRESHOLD.
     * - This provides visual feedback to the player during combat.
     */
    private void updateHealthBar()
    {
        final double healthRatio = (double) this.currentHp / this.maxHp;
        this.healthBar.setWidth(healthRatio * IMAGE_WIDTH_PX);

        if (healthRatio > HEALTH_HIGH_THRESHOLD)
        {
            this.healthBar.setFill(Color.LIMEGREEN);
        }
        else if (healthRatio > HEALTH_MEDIUM_THRESHOLD)
        {
            this.healthBar.setFill(Color.ORANGE);
        }
        else
        {
            this.healthBar.setFill(Color.RED);
        }
    }

    /**
     * Starts the enemy's scheduled attack loop using a JavaFX Timeline.
     * - The enemy attacks the player every 2 seconds.
     * - The damage dealt is determined solely by the current level of the game:
     *   - LEVEL_ONE   → DAMAGE_REGULAR
     *   - LEVEL_TWO   → DAMAGE_MID_BOSS
     *   - LEVEL_THREE → DAMAGE_BOSS
     * - Ensures both the enemy and the player are alive before attacking.
     * - If the player dies as a result of an attack:
     *   - The attack loop is stopped.
     *   - The player death callback is triggered on the JavaFX thread.
     * - The timeline runs indefinitely until stopped by enemy or player death.
     */
    private void startAttackTimer()
    {
        this.attackTimer = new Timeline(new KeyFrame(Duration.seconds(LEVEL_TWO), event ->
        {
            if (this.isAlive && this.playerRef != null && this.playerRef.isAlive())
            {
                if (this.currentLevel == LEVEL_ONE)
                {
                    damage = DAMAGE_REGULAR;
                }
                else if (this.currentLevel == LEVEL_TWO)
                {
                    damage = DAMAGE_MID_BOSS;
                }
                else if (this.currentLevel == LEVEL_THREE)
                {
                    damage = DAMAGE_BOSS;
                }
                else
                {
                    // fallback if level is unexpected
                    damage = DAMAGE_REGULAR;
                }

                this.playerRef.takeDamage(damage);

                if (!this.playerRef.isAlive() && this.onPlayerDeath != null)
                {
                    stopAttacking();
                    Platform.runLater(this.onPlayerDeath);
                }
            }
        }));
        this.attackTimer.setCycleCount(Timeline.INDEFINITE);
        this.attackTimer.play();
    }

    /**
     * Stops the tower’s attack loop.
     */
    public void stopAttacking()
    {
        if (this.attackTimer != null)
        {
            this.attackTimer.stop();
        }
    }

    /**
     * Checks if the tower enemy is still alive.
     *
     * @return true if alive, false if defeated
     */
    public boolean isAlive()
    {
        return this.isAlive;
    }

    /**
     * Returns the visual node for the enemy (image).
     *
     * @return the ImageView of this tower
     */
    public ImageView getView()
    {
        return this.view;
    }
}