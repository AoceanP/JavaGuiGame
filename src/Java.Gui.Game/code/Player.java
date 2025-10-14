/**
 * Represents the player in the game. Manages the player's stats, including health,
 * damage, and abilities. Handles health reduction, healing, stat allocation, and
 * player death logic.
 * The player can level up stats such as strength and health, and
 * can take damage or heal throughout the game. The player’s health is tracked
 * and updates whenever damage is taken or healing occurs.
 *
 * Dexterity and Intelligence have been removed from this version.
 * Strength directly impacts both HP and damage output.
 *
 * Author and documentation preserved from original implementation.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class Player {

    // === Constants ===
    private static final int DEFAULT_HP = 100;
    private static final int DEFAULT_STRENGTH = 0;
    private static final int DEFAULT_POINTS = 0;

    private static final int STRENGTH_HP_BONUS = 5;
    private static final int HP_UPGRADE_AMOUNT = 10;

    private static final int BASE_DAMAGE = 5;
    private static final int STRENGTH_DAMAGE_MULTIPLIER = 1; // Each STR gives 1 extra damage
    private static final int HEALTH_MIN = 0;
    private static final int DEAD_HP_THRESHOLD = 0;

    private int maxHp;
    private int currentHp;
    private int strength;
    private int points;
    private boolean alive;

    /**
     * Constructs a Player with default stats.
     * The player starts with 100 HP, 0 strength, and 0 points.
     */
    public Player() {
        this.maxHp = DEFAULT_HP;
        this.currentHp = DEFAULT_HP;
        this.strength = DEFAULT_STRENGTH;
        this.points = DEFAULT_POINTS;
        this.alive = true;
    }

    /**
     * Calculates the player's damage output based on strength.
     * The formula is base damage + (strength × multiplier).
     *
     * @return the calculated damage based on the player's strength
     */
    public int calculateDamage() {
        return BASE_DAMAGE + (strength * STRENGTH_DAMAGE_MULTIPLIER);
    }

    /**
     * Reduces the player's HP by a given damage amount.
     * If the damage reduces HP to 0 or below, the player is considered dead.
     *
     * @param damage the amount of damage to reduce from current HP
     */
    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp <= DEAD_HP_THRESHOLD) {
            currentHp = HEALTH_MIN;
            alive = false;
            System.out.println("Player has died.");
        } else {
            System.out.println("Player took " + damage + " damage. Current HP: " + currentHp);
        }
    }

    /**
     * Heals the player by a specified amount, ensuring the player’s health does not exceed max HP.
     *
     * @param amount the amount to heal the player by
     */
    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    /**
     * Retrieves the player's current HP.
     *
     * @return the current health of the player
     */
    public int getCurrentHp() {
        return currentHp;
    }

    /**
     * Retrieves the player's maximum HP.
     *
     * @return the maximum health of the player
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Retrieves the player's strength.
     *
     * @return the player's strength
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Retrieves the total points the player has earned for stat upgrades.
     *
     * @return the player's available points for stat upgrades
     */
    public int getPoints() {
        return points;
    }

    /**
     * Checks if the player is still alive.
     *
     * @return true if the player is alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets the player's current HP to a specific value, ensuring it stays within the valid range.
     * If the HP is set to 0 or below, the player is marked as dead.
     *
     * @param hp the new HP value to set
     */
    public void setCurrentHp(int hp) {
        this.currentHp = Math.max(HEALTH_MIN, Math.min(hp, maxHp));
        this.alive = currentHp > DEAD_HP_THRESHOLD;
    }

    /**
     * Increases the player's strength by 1 and raises the player's max HP by 5.
     * This represents the player getting stronger, which increases both their damage
     * output and their ability to withstand more damage.
     */
    public void addStrength() {
        strength++;
        maxHp += STRENGTH_HP_BONUS;
    }

    /**
     * Increases the player's maximum HP by 10. This provides the player with more
     * health and a greater ability to survive.
     */
    public void addHp() {
        maxHp += HP_UPGRADE_AMOUNT;
    }

    /**
     * Grants the player one skill point, which can be used for upgrading stats.
     * This function increases the number of points available for stat upgrades.
     */
    public void gainPoint() {
        points++;
    }

    /**
     * Resets all player stats and health to their initial values.
     * This function is useful for restarting the game or resetting the player's progress.
     */
    public void reset() {
        maxHp = DEFAULT_HP;
        currentHp = DEFAULT_HP;
        strength = DEFAULT_STRENGTH;
        points = DEFAULT_POINTS;
        alive = true;
    }
}