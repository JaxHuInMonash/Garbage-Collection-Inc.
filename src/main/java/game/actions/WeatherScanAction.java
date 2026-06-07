package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.alerts.WeatherAlert;
import game.weather.WeatherApiClient;
import game.weather.WeatherApiResponse;
import game.weather.effect.WeatherEffect;

import java.util.List;

/**
 * An Action that queries the OpenWeatherMap API using the player's current
 * map coordinates as latitude / longitude, then applies all registered
 * {@link WeatherEffect}s and {@link WeatherAlert}s to the game world.
 *
 * <p>This class depends strictly on the {@link WeatherEffect} and
 * {@link WeatherAlert} abstractions — it never references concrete
 * implementations directly (Dependency Inversion Principle).
 */
public class WeatherScanAction extends Action {

    private final List<WeatherEffect> effects;
    private final List<WeatherAlert> alerts;
    private final WeatherApiClient apiClient;

    /**
     * Constructor.
     *
     * @param effects the weather effects to apply after fetching data
     * @param alerts  the weather alerts to check and apply
     */
    public WeatherScanAction(List<WeatherEffect> effects, List<WeatherAlert> alerts) {
        this.effects = effects;
        this.alerts = alerts;
        this.apiClient = new WeatherApiClient();
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);

        // API parameters dynamically driven by game state (player coordinates)
        WeatherApiResponse response = apiClient.fetch(here.y(), here.x());

        StringBuilder sb = new StringBuilder();
        sb.append("=== Weather Scan at (").append(here.x()).append(", ").append(here.y()).append(") ===");
        sb.append("\n  Temperature: ").append(response.temperature()).append("°C");
        sb.append("\n  Humidity: ").append(response.humidity()).append("%");
        sb.append("\n  Wind: ").append(response.windSpeed()).append(" m/s");
        sb.append("\n  Condition: ").append(response.condition());

        // Apply all effects through their abstraction
        sb.append("\n--- Effects ---");
        for (WeatherEffect effect : effects) {
            String result = effect.apply(actor, map, here, response);
            if (!result.isEmpty()) sb.append("\n").append(result);
        }

        // Check and apply alerts through their abstraction
        boolean anyAlert = false;
        for (WeatherAlert alert : alerts) {
            if (alert.isTriggered(response)) {
                if (!anyAlert) {
                    sb.append("\n--- Alerts ---");
                    anyAlert = true;
                }
                sb.append("\n").append(alert.apply(actor, map, here, response));
            }
        }
        if (!anyAlert) sb.append("\nNo weather alerts.");

        return sb.toString();
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " scans the environment for weather data";
    }
}
