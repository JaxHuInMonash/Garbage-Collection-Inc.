package game.capabilities;

import edu.monash.fit2099.engine.positions.Location;
import game.weather.WeatherApiResponse;
import game.weather.effect.TerrainEffect;

/**
 * Interface for ground tiles that react to weather conditions.
 *
 * <p>Implementing tiles receive a callback via {@link #onWeatherChange} whenever
 * a weather scan is performed, allowing them to transform or update themselves
 * based on the latest {@link WeatherApiResponse} — no polling in {@code tick()},
 * no static holders, no coupling to concrete weather logic.
 *
 * <p>Dispatch is handled by {@link TerrainEffect}, which iterates the map
 * and calls this method on every {@code WeatherAffected} tile after a scan.
 */
public interface WeatherAffected {

    /**
     * Called when a weather scan completes and new data is available.
     *
     * @param location the location of this tile
     * @param weather  the latest weather data from the API
     * @return Weather there was a change or not
     */
    boolean onWeatherChange(Location location, WeatherApiResponse weather);
}
