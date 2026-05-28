package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Abstraction representing a game-world effect triggered by weather data.
 *
 * Each concrete implementation selects the {@link WeatherDataPoint}s it needs
 * from the supplied list and alters game state accordingly — affecting actor
 * HP, spawning creatures, or transforming terrain.
 *
 * Concrete implementations: {@link HealthEffect}, {@link SpawnRateEffect},
 * {@link TerrainEffect}.
 */
public interface WeatherEffect {

    /**
     * Applies this effect to the game world given the current weather data.
     *
     * @param actor     the actor context (may be null for automatic processing)
     * @param map       the current game map
     * @param location  the location context
     * @param dataPoints all available weather data points
     * @return a human-readable description of what happened
     */
    String apply(Actor actor, GameMap map, Location location, List<WeatherDataPoint> dataPoints);
}
