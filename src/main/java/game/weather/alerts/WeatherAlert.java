package game.weather.alerts;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.WeatherApiResponse;

/**
 * Abstraction representing a severe-weather alert that triggers an additional
 * game-state change when extreme conditions are detected.
 *
 * Each implementation checks a different weather threshold ({@link #isTriggered})
 * and applies a distinct gameplay consequence ({@link #apply}) — fanning fires
 * in high wind, extinguishing them in rain, intensifying status effects in
 * extreme temperatures, etc.
 *
 * Concrete implementations: {@link ExtremeTemperatureAlert},
 * {@link HighWindAlert}, {@link HeavyRainAlert}.
 */
public interface WeatherAlert {

    /**
     * Returns true if the current weather data meets this alert's threshold.
     *
     * @param weather the parsed API response
     * @return true if the alert should fire
     */
    boolean isTriggered(WeatherApiResponse weather);

    /**
     * Applies this alert's gameplay consequence.
     *
     * @param actor    the actor context
     * @param map      the current game map
     * @param location the location context
     * @param weather  the parsed API response
     * @return a description of what happened
     */
    String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather);
}
