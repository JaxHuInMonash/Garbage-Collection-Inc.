package game.weather.effect;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.WeatherApiResponse;

/**
 * Abstraction representing a game-world effect triggered by weather data.
 * Each concrete implementation reads the fields it needs directly from the
 * {@link WeatherApiResponse} record — no intermediate data objects, no
 * {@code instanceof} checks.
 *
 * Concrete implementations: {@link HealthEffect}, {@link SpawnRateEffect},
 * {@link TerrainEffect}.
 */
public interface WeatherEffect {

    /**
     * Applies this effect to the game world given the current weather data.
     *
     * @param actor    the actor context
     * @param map      the current game map
     * @param location the location context
     * @param weather  the parsed API response with raw temperature/humidity/wind
     * @return a human-readable description of what happened
     */
    String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather);
}
