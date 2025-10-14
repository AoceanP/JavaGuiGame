/**
 * AbstractGame is a reusable base class for games that track performance statistics.
 * It keeps track of the number of games played, games won, and successful placements.
 * Subclasses can use this for shared behavior without duplicating logic.
 * -
 * Fields are private for proper encapsulation, and access is provided via public getters.
 * This ensures internal game state cannot be altered from outside the class.
 * -
 * @author Aleksandar Panich
 * @version 1.0
 */
public abstract class AbstractGame
{
    private static final int MIN_GAMES = 0;
    private static final int SINGLE_GAME = 1;

    /**
     * Total number of games played.
     */
    private int gamesPlayed;

    /**
     * Total number of games won.
     */
    private int gamesWon;

    /**
     * Total number of successful placements across all games.
     */
    private int totalPlacements;

    /**
     * Constructs a new AbstractGame with all stats initialized to zero.
     */
    public AbstractGame()
    {
        this.gamesPlayed = MIN_GAMES;
        this.gamesWon = MIN_GAMES;
        this.totalPlacements = MIN_GAMES;
    }

    /**
     * Increments the number of games played by one.
     * This should be called every time a game session is completed.
     */
    public void incrementGamesPlayed()
    {
        gamesPlayed++;
    }

    /**
     * Increments the number of games won by one.
     * This should be called whenever a game ends in a win condition.
     */
    public void incrementGamesWon()
    {
        gamesWon++;
    }

    /**
     * Adds the given number of placements to the total successful placements counter.
     * This represents how many correct or valid inputs the player made across the game.
     *
     * @param count the number of successful placements to add
     */
    public void addPlacements(final int count)
    {
        totalPlacements += count;
    }

    /**
     * Returns the number of games played.
     *
     * @return number of games played
     */
    public int getGamesPlayed()
    {
        return gamesPlayed;
    }

    /**
     * Returns the number of games won.
     *
     * @return number of games won
     */
    public int getGamesWon()
    {
        return gamesWon;
    }

    /**
     * Returns the total number of successful placements.
     *
     * @return number of successful placements
     */
    public int getTotalPlacements()
    {
        return totalPlacements;
    }

    /**
     * Prints the player's performance statistics to the console in a human-readable format.
     * The statistics include:
     * - Total number of games played
     * - Total number of games won or lost (based on outcome)
     * - Total number of successful placements made during all games.
     * If no games have been played yet, the average placements will default to 0.0.
     * The method uses conditional logic to ensure the correct grammar is used
     */
    public void printStats()
    {
        final int gamesLost = gamesPlayed - gamesWon;

        final double avg;
        if (gamesPlayed == MIN_GAMES)
        {
            avg = 0.0;
        }
        else
        {
            avg = (double) totalPlacements / gamesPlayed;
        }

        final String outcome;
        final int outcomeCount;

        if (gamesWon == MIN_GAMES)
        {
            outcome = "lost";
            outcomeCount = gamesLost;
        }
        else
        {
            outcome = "won";
            outcomeCount = gamesWon;
        }

        final String plural;
        if (gamesPlayed == SINGLE_GAME)
        {
            plural = "";
        }
            else
        {
            plural = "s";
        }

        System.out.printf(
                "You %s %d out of %d game%s, with %d successful placements, an average of %.2f per game.%n",
                outcome,
                outcomeCount,
                gamesPlayed,
                plural,
                totalPlacements,
                avg
        );
    }

    /**
     * Resets all statistics to zero.
     * Useful for restarting game sessions or clearing player performance data.
     */
    public void resetStats()
    {
        gamesPlayed = MIN_GAMES;
        gamesWon = MIN_GAMES;
        totalPlacements = MIN_GAMES;
    }
}