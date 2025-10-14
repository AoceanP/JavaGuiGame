/**
 * The Country class represents a single country and its associated capital city.
 * This class is used in geography-related games to generate quiz questions that
 * ask the player to identify the capital of a specific country.
 * Each Country object contains a country name and its capital city. It is designed
 * to be immutable: once created, its values cannot be changed.
 * The third constructor parameter is accepted but currently ignored. It exists solely
 * to match an expected method signature used by the WordGame file loader and may be used
 * in the future to associate additional facts or metadata about Canada.
 * Example usage:
 * - new Country("Canada", "Ottawa", unusedArray);
 * - Used in game logic to display: "What is the capital of Canada?"
 * This class is deliberately simple and single-purpose to follow clean design principles.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class Country
{
    /**
     * The name of the country.
     */
    private final String name;

    /**
     * The name of the capital city of the country.
     */
    private final String capitalCityName;

    /**
     * Constructs a new Country object with the specified country name and capital city name.
     * This constructor also accepts a third parameter for compatibility with the WordGame loader,
     * which expects three arguments when creating Country objects. Although the third parameter
     * (canadaFacts) is currently unused, it is provided to ensure forward compatibility
     * and potential extensibility for including additional data later.
     *
     * @param name            the name of the country
     * @param capitalCityName the name of the capital city of the country
     * @param canadaFacts     an unused placeholder for future metadata related to Canadian countries or facts
     */
    public Country(final String name,
                   final String capitalCityName,
                   final String[] canadaFacts)
    {
        this.name = name;
        this.capitalCityName = capitalCityName;
    }

    /**
     * Returns the name of the country.
     *
     * @return the name of the country
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the name of the capital city associated with this country.
     *
     * @return the capital city name
     */
    public String getCapitalCityName()
    {
        return capitalCityName;
    }
}