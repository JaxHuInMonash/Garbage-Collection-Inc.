package game.weather;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * An Action that queries the OpenWeatherMap API using the player's current
 * map coordinates as latitude / longitude, then applies all registered
 * {@link WeatherEffect}s to the game world.
 *
 * <p>This class depends strictly on the {@link WeatherDataPoint} and
 * {@link WeatherEffect} abstractions — it never references concrete
 * implementations directly (Dependency Inversion Principle).
 */
public class WeatherScanAction extends Action {

    private final List<Supplier<WeatherDataPoint>> dataPointSuppliers;
    private final List<WeatherEffect> effects;
    private final WeatherApiClient apiClient;

    /**
     * Constructor.
     *
     * @param dataPointSuppliers suppliers that create fresh {@link WeatherDataPoint}
     *                           instances each time the action is executed
     * @param effects            the weather effects to apply after fetching data
     */
    public WeatherScanAction(List<Supplier<WeatherDataPoint>> dataPointSuppliers,
                             List<WeatherEffect> effects) {
        this.dataPointSuppliers = dataPointSuppliers;
        this.effects = effects;
        this.apiClient = new WeatherApiClient();
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);

        // API parameters are dynamically driven by game state (player coordinates)
        double lat = here.y();
        double lon = here.x();

        // Fetch weather data from the API
        WeatherApiResponse response = apiClient.fetch(lat, lon);

        // Build data points from the abstractions (DIP: never references concretions)
        List<WeatherDataPoint> dataPoints = new ArrayList<>();
        for (Supplier<WeatherDataPoint> supplier : dataPointSuppliers) {
            WeatherDataPoint dp = supplier.get();
            dp.readFrom(response);
            dataPoints.add(dp);
        }

        // Log the weather data
        StringBuilder sb = new StringBuilder();
        sb.append("=== Weather Scan at (").append(here.x()).append(", ").append(here.y()).append(") ===");
        for (WeatherDataPoint dp : dataPoints) {
            sb.append("\n  ").append(dp.getLabel());
        }
        sb.append("\n  Condition: ").append(response.getCondition());

        // Apply all effects through their abstractions (DIP)
        sb.append("\n--- Effects ---");
        for (WeatherEffect effect : effects) {
            String result = effect.apply(actor, map, here, dataPoints);
            if (!result.isEmpty()) {
                sb.append("\n").append(result);
            }
        }

        return sb.toString();
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " scans the environment for weather data";
    }
}
