package game.weather.effect;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.WeatherAffected;
import game.weather.WeatherApiResponse;

/**
 * A {@link WeatherEffect} that dispatches the latest weather data to all
 * {@link WeatherAffected} ground tiles on the map, allowing them to react
 * (e.g. flood, evaporate) at scan time rather than polling in {@code tick()}.
 *
 * <p>No concrete ground types are imported — tiles self-determine their own
 * response through the {@link WeatherAffected} interface.
 */
public class TerrainEffect implements WeatherEffect {

    @Override
    public String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather) {
        int count = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                WeatherAffected ground = loc.getGroundAs(WeatherAffected.class);
                if (ground != null) { // Not ideal but we can't modify engine ground abstraction so this is required
                    boolean success = ground.onWeatherChange(loc, weather);

                    if  (success) count++;
                }
            }
        }

        if (count == 0) {
            return "No weather-reactive terrain effected.";
        }
        return count + " weather-reactive tile(s) responded to the scan.";
    }
}
