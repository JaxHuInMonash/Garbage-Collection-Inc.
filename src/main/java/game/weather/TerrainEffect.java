package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.grounds.Dirt;
import game.grounds.Puddle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A {@link WeatherEffect} that transforms terrain tiles based on humidity,
 * checking existing ground abilities before making changes.
 *
 * <ul>
 *   <li>High humidity (&ge; 0.7) → converts up to 3 Dirt tiles adjacent to
 *       existing Puddles into new Puddles (flooding)</li>
 *   <li>Low humidity (&le; 0.3) → converts up to 3 Puddle tiles into Dirt
 *       (evaporation)</li>
 * </ul>
 *
 * Transformations respect the existing {@link Ability#BLOCKS_ENTRY} marker:
 * tiles that block entry (walls, doors, etc.) are never transformed, even
 * if they match the target ground type.
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
     * Floods Dirt tiles adjacent to existing Puddles.
     * Tiles that block entry (walls, doors) are never transformed.
     */
    private String floodTerrain(GameMap map) {
        List<Location> floodable = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround() instanceof Dirt
                        && isAdjacentToPuddle(loc)) {
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
     * Evaporates Puddle tiles into Dirt. Puddles that block entry
     * (if any exist) are left as-is.
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
