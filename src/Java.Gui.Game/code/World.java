import java.util.HashMap;

/**
 * Represents a collection of Country objects organized by country name.
 * The World class acts as a container for storing and managing a global set of countries,
 * where each country is uniquely identified by its name. Internally, it uses a HashMap to
 * efficiently retrieve, add, and manage Country instances.
 * - countries: A HashMap mapping country names (String) to Country objects. This structure
 *   allows for quick lookups and ensures uniqueness based on the country name.
 *
 * @author Aleksandar Panich
 * @version 1.0
 */
public class World
{
    private final HashMap<String, Country> countries;

    /**
     * Constructs a new, empty World object.
     * Initializes the internal HashMap used to store countries.
     */
    public World()
    {
        countries = new HashMap<>();
    }

    /**
     * Adds a Country object to the World.
     * If a country with the same name already exists, it will be replaced.
     *
     * @param country The Country object to add.
     */
    public void addCountry(Country country)
    {
        countries.put(country.getName(), country);
    }

    /**
     * Returns the internal HashMap of countries.
     * This can be used for iterating over or inspecting all countries in the World.
     *
     * @return A HashMap mapping country names to Country objects.
     */
    public HashMap<String, Country> getCountries()
    {
        return countries;
    }

    /**
     * Retrieves a specific Country object by its name.
     *
     * @param name The name of the country to retrieve.
     * @return The Country object with the given name, or null if not found.
     */
    public Country getCountryByName(String name)
    {
        return countries.get(name);
    }
}