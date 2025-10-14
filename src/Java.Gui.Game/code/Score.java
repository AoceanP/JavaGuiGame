import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Score class represents a single word game session's results. It records when the session
 * took place, how many games were played, and how many were answered correctly or incorrectly.
 * This class also handles saving and loading scores to and from files for persistent tracking.
 * The score for each session is calculated as follows:
 * - Correct first attempts earn 2 points.
 * - Correct second attempts earn 1 point.
 * This class supports the functionality of:
 * - Storing the session's results.
 * - Writing session data to a file in CSV and user-friendly formats.
 * - Loading session data from a file.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class Score
{
    private final LocalDateTime dateTimePlayed;
    private final int numGamesPlayed;
    private final int numCorrectFirstAttempt;
    private final int numCorrectSecondAttempt;
    private final int numIncorrectTwoAttempts;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a Score object using the current system time as the date played.
     * This constructor is used when a session has just been completed.
     *
     * @param games     Total number of word games played during the session.
     * @param correct1  Number of correct answers on the first attempt.
     * @param correct2  Number of correct answers on the second attempt.
     * @param incorrect Number of incorrect answers after two attempts.
     */
    public Score(final int games,
                 final int correct1,
                 final int correct2,
                 final int incorrect)
    {
        this.dateTimePlayed = LocalDateTime.now();
        this.numGamesPlayed = games;
        this.numCorrectFirstAttempt = correct1;
        this.numCorrectSecondAttempt = correct2;
        this.numIncorrectTwoAttempts = incorrect;
    }

    /**
     * Constructs a Score object with a specific time.
     * This constructor is used when loading past game records from a file.
     *
     * @param dateTime  The exact time the session was played.
     * @param games     Total number of word games played during the session.
     * @param correct1  Number of correct answers on the first attempt.
     * @param correct2  Number of correct answers on the second attempt.
     * @param incorrect Number of incorrect answers after two attempts.
     */
    public Score(final LocalDateTime dateTime,
                 final int games,
                 final int correct1,
                 final int correct2,
                 final int incorrect)
    {
        this.dateTimePlayed = dateTime;
        this.numGamesPlayed = games;
        this.numCorrectFirstAttempt = correct1;
        this.numCorrectSecondAttempt = correct2;
        this.numIncorrectTwoAttempts = incorrect;
    }

    /**
     * Calculates the total score earned during the session.
     * First attempt correct answers are worth 2 points, while second attempt correct answers are worth 1 point.
     *
     * @return The total score calculated from correct answers on the first and second attempts.
     */
    public int getScore()
    {
        return (2 * numCorrectFirstAttempt) + numCorrectSecondAttempt;
    }

    /**
     * Returns a user-friendly formatted string summarizing the session.
     * This includes the date played, number of games, number of attempts (correct and incorrect), and the total score.
     *
     * @return A formatted string containing the session details.
     */
    @Override
    public String toString()
    {
        return String.format(
                "Date and Time: %s\n" +
                        "Games Played: %d\n" +
                        "Correct First Attempts: %d\n" +
                        "Correct Second Attempts: %d\n" +
                        "Incorrect Attempts: %d\n" +
                        "Score: %d points\n",
                dateTimePlayed.format(formatter),
                numGamesPlayed,
                numCorrectFirstAttempt,
                numCorrectSecondAttempt,
                numIncorrectTwoAttempts,
                getScore()
        );
    }

    /**
     * Writes this Score object to the specified file in CSV format.
     * The format follows: dateTime, games, correct1, correct2, incorrect.
     * This method appends the new score to the existing file content.
     *
     * @param score    The score object to save.
     * @param filename The file to write the score to.
     * @throws IOException if writing to the file fails.
     */
    public static void appendScoreToFile(final Score score,
                                         final String filename) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true)))
        {
            writer.write(score.dateTimePlayed.format(formatter) + "," +
                    score.numGamesPlayed + "," +
                    score.numCorrectFirstAttempt + "," +
                    score.numCorrectSecondAttempt + "," +
                    score.numIncorrectTwoAttempts + "\n");
        }
    }

    /**
     * Writes this Score object to the specified file in a user-friendly format.
     * Each score is saved with labels and line breaks for easy readability.
     *
     * @param score    The score object to append in readable format.
     * @param filename The file to write to.
     * @throws IOException if writing to the file fails.
     */
    public static void appendFormattedScoreToFile(final Score score,
                                                  final String filename) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true)))
        {
            writer.write(score.toString());
            writer.write("\n");
        }
    }

    /**
     * Reads and parses Score records from a CSV-formatted file to restore previously saved
     * word game session data. Each line in the file contains the results of a single game session,
     * including the date played, number of games, and performance statistics.
     *
     * This method uses a BufferedReader to efficiently process each line and load the data into
     * Score objects. Each record in the file represents a completed game session.
     *
     * @param filename The file to read Score data from.
     * @return A list of Score objects parsed from the valid lines in the file.
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static List<Score> readScoresFromFile(final String filename) throws IOException
    {
        List<Score> scores = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists())
        {
            return scores; // Return an empty list if the file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.isEmpty()) // Skip empty lines
                {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 5) { // Skip invalid lines
                    continue;
                }

                try
                {
                    LocalDateTime dateTime = LocalDateTime.parse(parts[0], formatter);
                    final int games = Integer.parseInt(parts[1]);
                    final int correct1 = Integer.parseInt(parts[2]);
                    final int correct2 = Integer.parseInt(parts[3]);
                    final int incorrect = Integer.parseInt(parts[4]);

                    Score score = new Score(dateTime, games, correct1, correct2, incorrect);
                    scores.add(score); // Add the parsed score to the list
                }
                catch (NumberFormatException | DateTimeParseException e)
                {
                    System.out.println("Invalid data: " + line); // Log invalid data and skip
                }
            }
        }

        return scores; // Return the list of scores loaded from the file
    }

    /**
     * Gets the timestamp of when the game session was played.
     *
     * @return the LocalDateTime of the session when it was played.
     */
    public LocalDateTime getDateTimePlayed()
    {
        return dateTimePlayed;
    }

    /**
     * Gets the number of word games played during the session.
     *
     * @return the number of games played in the session.
     */
    public int getNumGamesPlayed()
    {
        return numGamesPlayed;
    }

    /**
     * Gets the count of words guessed correctly on the first attempt.
     *
     * @return the number of first-attempt correct answers.
     */
    public int getNumCorrectFirstAttempt()
    {
        return numCorrectFirstAttempt;
    }

    /**
     * Gets the count of words guessed correctly on the second attempt.
     *
     * @return the number of second-attempt correct answers.
     */
    public int getNumCorrectSecondAttempt()
    {
        return numCorrectSecondAttempt;
    }

    /**
     * Gets the count of words that were not guessed correctly after two tries.
     *
     * @return the number of incorrect attempts.
     */
    public int getNumIncorrectTwoAttempts()
    {
        return numIncorrectTwoAttempts;
    }
}