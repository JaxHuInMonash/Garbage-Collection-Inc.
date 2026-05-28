package game.weather;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.Slime;
import game.actors.Undead;
import game.spawning.SpawnEffectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A {@link WeatherEffect} that spawns or removes creatures based on humidity
 * and temperature conditions.
 *
 * <ul>
 *   <li>Hot + Humid (temp &ge; 0.5 AND humidity &ge; 0.6) → spawns a creature</li>
 *   <li>Cold + Dry (temp &le; 0.25 AND humidity &le; 0.3) → removes a creature</li>
 * </ul>
 *
 * Creature type is chosen based on conditions: hot+humid spawns Slime,
 * hot+dry spawns Undead, mild+humid spawns Parasite.
 */
public class SpawnRateEffect implements WeatherEffect {

    private static final double HOT_THRESHOLD = 0.50;
    private static final double HUMID_THRESHOLD = 0.60;
    private static final double COLD_THRESHOLD = 0.25;
    private static final double DRY_THRESHOLD = 0.30;

    private static final int DESPAWN_ATTEMPTS = 10;

    private final Random random = new Random();

    @Override
    public String apply(Actor actor, GameMap map, Location location, List<WeatherDataPoint> dataPoints) {
        TemperatureDataPoint temp = findDataPoint(dataPoints, TemperatureDataPoint.class);
        HumidityDataPoint humidity = findDataPoint(dataPoints, HumidityDataPoint.class);

        if (temp == null || humidity == null) {
            return "";
        }

        double t = temp.getNormalisedValue();
        double h = humidity.getNormalisedValue();

        if (t >= HOT_THRESHOLD && h >= HUMID_THRESHOLD) {
            Actor creature = chooseCreature(t, h);
            return spawnNearPlayer(map, location, creature);
        }

        if (t <= COLD_THRESHOLD && h <= DRY_THRESHOLD) {
            return despawnRandomCreature(map);
        }

        return "Spawn rates unaffected by current weather.";
    }

    private Actor chooseCreature(double temperature, double humidity) {
        if (humidity >= 0.8) {
            return new Slime();
        } else if (temperature >= 0.7) {
            return new Undead();
        } else {
            return new Parasite();
        }
    }

    private String spawnNearPlayer(GameMap map, Location playerLocation, Actor creature) {
        List<Location> validLocations = new ArrayList<>();
        for (Exit exit : playerLocation.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(creature)) {
                validLocations.add(dest);
            }
        }

        if (validLocations.isEmpty()) {
            return "No space nearby for a creature to spawn.";
        }

        Location spawnPoint = validLocations.get(random.nextInt(validLocations.size()));
        try {
            spawnPoint.addActor(creature);
            SpawnEffectManager.getInstance().onCreatureSpawned(creature, spawnPoint);
            return creature + " spawned by the weather conditions near " + playerLocation + "!";
        } catch (GameEngineException e) {
            return "Failed to spawn creature.";
        }
    }

    private String despawnRandomCreature(GameMap map) {
        List<Actor> removable = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (!target.hasAbility(game.capabilities.Ability.ATTACKABLE_BY_CREATURES)) {
                        continue; // don't despawn the player/workers
                    }
                    removable.add(target);
                }
            }
        }

        if (removable.isEmpty()) {
            return "No creatures to despawn.";
        }

        for (int i = 0; i < DESPAWN_ATTEMPTS && !removable.isEmpty(); i++) {
            Actor target = removable.get(random.nextInt(removable.size()));
            if (target.isConscious()) {
                target.hurt(999);
                map.removeActor(target);
                return "Harsh weather claims " + target + " — it freezes in the cold.";
            }
        }

        return "Could not find a creature to despawn.";
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
