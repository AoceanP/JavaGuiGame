import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

/**
 * Runs an interactive trivia game that quizzes the user on capital cities.
 * - Loads country and capital data from external text files.
 * - Selects random countries to ask the player for their capitals.
 * - Tracks score and saves performance at the end of each session.
 * - Supports playing multiple rounds in one session.
 * Game Rules:
 * - 10 questions per round.
 * - 2 attempts per question.
 * - Capital spelling is case-insensitive.
 * - Stats shown after each round, persisted on exit.
 * Dependencies:
 * - Relies on a `Country` class for holding country/capital pairs.
 * - Outputs final score to `score.txt`.
 * Error Handling:
 * - Continues gracefully on file read issues.
 * - Notifies the user if loading or saving fails.
 * - Displays high score info based on calculated points-per-game.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class WordGame
{
    private static final int QUESTIONS_PER_ROUND = 10;
    private static final int MAX_FILE_PARTS = 2;
    private static final String SCORE_FILE_NAME = "score.txt";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Loads country-capital data from a folder of `.txt` files.
     * - Each line must be in format: CountryName,CapitalCity
     * - Ignores malformed lines or missing files.
     * - Accepts only `.txt` files from the folder.
     * Behavior:
     * - Skips unreadable files but prints error messages to System.err.
     * - Throws IOException if no valid files are found.
     *
     * @param directoryPath path to the folder containing text files
     * @return list of Country objects parsed from files
     * @throws IOException if folder is invalid or unreadable
     */
    public static List<Country> loadCountriesFromDirectory(final String directoryPath) throws IOException
    {
        final List<Country> countries = new ArrayList<>();
        final File folder = new File(directoryPath);
        final File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null)
        {
            throw new IOException("Directory not found or is empty: " + directoryPath);
        }

        for (File file : files)
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    final String[] parts = line.split(",");
                    if (parts.length == MAX_FILE_PARTS)
                    {
                        countries.add(new Country(parts[0].trim(), parts[1].trim(), null));
                    }
                }
            }
            catch (IOException e)
            {
                System.out.println("Failed to read file: " + file.getName() + " -> " + e.getMessage());
            }
        }

        return countries;
    }

    /**
     * Runs the main loop for the Word Game trivia session.
     * - Randomly selects 10 countries per round.
     * - Prompts the user to guess each capital (2 attempts).
     * - Tracks results (first try, second try, failures).
     * - Displays round statistics and cumulative results.
     * - Prompts to play again or exit.
     * - On exit, saves stats to `score.txt` and checks for new high score.
     * Input Handling:
     * - All user input is read via Scanner from System.in.
     * - Input is case-insensitive and trimmed.
     * - Invalid play-again responses are re-prompted until valid.
     *
     * @param countries the list of playable countries to quiz from
     */
    public static void playGame(final List<Country> countries)
    {
        try
        {
            final Scanner scanner = new Scanner(System.in);
            final Random random = new Random();

            int totalGames = 0;
            int firstTry = 0;
            int secondTry = 0;
            int failed = 0;

            boolean keepPlaying = true;

            while (keepPlaying)
            {
                int correct1 = 0;
                int correct2 = 0;
                int incorrect = 0;

                for (int i = 0; i < QUESTIONS_PER_ROUND; i++)
                {
                    final Country selected = countries.get(random.nextInt(countries.size()));
                    final String answer = selected.getCapitalCityName();

                    System.out.println("What is the capital of " + selected.getName() + "?");
                    System.out.print("Your guess: ");
                    final String guess1 = scanner.nextLine().trim();

                    if (guess1.equalsIgnoreCase(answer))
                    {
                        System.out.println("CORRECT");
                        correct1++;
                        continue;
                    }

                    System.out.println("INCORRECT. Try again.");
                    System.out.print("Second guess: ");
                    final String guess2 = scanner.nextLine().trim();

                    if (guess2.equalsIgnoreCase(answer))
                    {
                        System.out.println("CORRECT");
                        correct2++;
                    }
                    else
                    {
                        System.out.println("INCORRECT. The correct answer was " + answer);
                        incorrect++;
                    }
                }

                totalGames++;
                firstTry += correct1;
                secondTry += correct2;
                failed += incorrect;

                System.out.println();
                System.out.println("- " + totalGames + (totalGames == 1 ? " word game played" : " word games played"));
                System.out.println("- " + firstTry + " correct answers on the first attempt");
                System.out.println("- " + secondTry + " correct answers on the second attempt");
                System.out.println("- " + failed + " incorrect answers on two attempts each");
                System.out.println();

                System.out.print("Do you want to play again? (Yes/No): ");
                String input = scanner.nextLine().trim();

                while (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no"))
                {
                    System.out.println("Invalid response. Please enter Yes or No.");
                    System.out.print("Do you want to play again? ");
                    input = scanner.nextLine().trim();
                }

                if (input.equalsIgnoreCase("no"))
                {
                    // Create a new score object and save it to file
                    final Score finalScore = new Score(totalGames, firstTry, secondTry, failed);
                    Score.appendFormattedScoreToFile(finalScore, SCORE_FILE_NAME);

                    // Calculate the average score for the current session
                    double finalAvg = (double) finalScore.getScore() / finalScore.getNumGamesPlayed();

                    // Load all existing scores from the file to find the highest previous average
                    List<Score> allScores = Score.readScoresFromFile(SCORE_FILE_NAME);
                    Score best = null;
                    double bestAvg = 0;

                    for (Score s : allScores)
                    {
                        double avg = (double) s.getScore() / s.getNumGamesPlayed();
                        if (avg > bestAvg)
                        {
                            bestAvg = avg;
                            best = s;
                        }
                    }

                    // Determine if the new score beats the previous best
                    if (best == null || finalAvg > bestAvg)
                    {
                        System.out.printf("CONGRATULATIONS! You are the new high score with an " +
                                "average of %.2f points per game", finalAvg);
                        if (best != null)
                        {
                            System.out.printf("; the previous record was %.2f points per game on %s\n",
                                    bestAvg,
                                    best.getDateTimePlayed().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
                        }
                        else
                        {
                            System.out.println("; this is the first recorded score.");
                        }
                    }
                    else
                    {
                        System.out.printf("You did not beat the high score of %.2f points per game from %s\n",
                                bestAvg,
                                best.getDateTimePlayed().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
                    }

                    // Exit the game loop
                    keepPlaying = false;
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred during the game: " + e.getMessage());
        }
    }
}