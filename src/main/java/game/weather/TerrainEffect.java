package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Dirt;
import game.grounds.Puddle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A {@link WeatherEffect} that transforms ground tiles based on humidity
 * and wind conditions.
 *
 * <ul>
 *   <li>High humidity (&ge; 0.7) → Dirt tiles adjacent to Puddles become Puddles (flooding)</li>
 *   <li>Low humidity (&le; 0.3) → Puddle tiles become Dirt (evaporation)</li>
 *   <li>High wind (&ge; 0.6) → Fires a single Dirt tile turns into... well, it just dries out faster</li>
 * </ul>
 *
 * This directly creates and destroys ground objects on the map.
 */
public class TerrainEffect implements WeatherEffect {

    private static final double FLOOD_THRESHOLD = 0.7;
    private static final double EVAPORATE_THRESHOLD = 0.3;
    private static final int MAX_TRANSFORMATIONS = 3;

    private final Random random = new Random();

    @Override
    public String apply(Actor actor, GameMap map, Location location, List<WeatherDataPoint> dataPoints) {
        HumidityDataPoint humidity = findDataPoint(dataPoints, HumidityDataPoint.class);
        if (humidity == null) {
            return "";
        }

        double h = humidity.getNormalisedValue();

        if (h >= FLOOD_THRESHOLD) {
            return floodTerrain(map);
        } else if (h <= EVAPORATE_THRESHOLD) {
            return evaporateTerrain(map);
        }

        return "Ground conditions stable — no terrain changes.";
    }

    /**
     * Floods up to {@value MAX_TRANSFORMATIONS} Dirt tiles that are adjacent to
     * existing Puddles, turning them into new Puddles.
     */
    private String floodTerrain(GameMap map) {
        List<Location> floodable = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround() instanceof Dirt && isAdjacentToPuddle(loc)) {
                    floodable.add(loc);
                }
            }
        }

        if (floodable.isEmpty()) {
            return "Humidity is high, but no Dirt near Puddles to flood.";
        }

        int transformed = 0;
        for (int i = 0; i < MAX_TRANSFORMATIONS && !floodable.isEmpty(); i++) {
            Location target = floodable.remove(random.nextInt(floodable.size()));
            target.setGround(new Puddle());
            transformed++;
        }

        return "High humidity! " + transformed + " Dirt tile(s) flooded into Puddles.";
    }

    /**
     * Evaporates up to {@value MAX_TRANSFORMATIONS} Puddle tiles into Dirt.
     */
    private String evaporateTerrain(GameMap map) {
        List<Location> evaporatable = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround() instanceof Puddle) {
                    evaporatable.add(loc);
                }
            }
        }

        if (evaporatable.isEmpty()) {
            return "Low humidity, but no Puddles to evaporate.";
        }

        int transformed = 0;
        for (int i = 0; i < MAX_TRANSFORMATIONS && !evaporatable.isEmpty(); i++) {
            Location target = evaporatable.remove(random.nextInt(evaporatable.size()));
            target.setGround(new Dirt());
            transformed++;
        }

        return "Low humidity! " + transformed + " Puddle tile(s) evaporated into Dirt.";
    }

    private boolean isAdjacentToPuddle(Location location) {
        for (Exit exit : location.getExits()) {
            if (exit.getDestination().getGround() instanceof Puddle) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T extends WeatherDataPoint> T findDataPoint(List<WeatherDataPoint> points, Class<T> type) {
        for (WeatherDataPoint dp : points) {
            if (type.isInstance(dp)) {
                return (T) dp;
            }
        }
        return null;
    }
}
