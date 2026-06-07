package game.weather.effect;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.Slime;
import game.actors.Undead;
import game.capabilities.Ability;
import game.spawning.SpawnEffectManager;
import game.weather.WeatherApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A {@link WeatherEffect} that spawns or removes creatures based on humidity
 * and temperature, tracked by the existing {@link SpawnEffectManager}.
 *
 * <ul>
 *   <li>Temp &ge; 30°C AND humidity &ge; 60% → spawns a creature near the player</li>
 *   <li>Temp &le; 0°C AND humidity &le; 30% → removes a hostile creature</li>
 * </ul>
 */
public class SpawnRateEffect implements WeatherEffect {

    private static final double HOT_TEMP = 30.0;
    private static final double HUMID_PCT = 60;
    private static final double COLD_TEMP = 0.0;
    private static final double DRY_PCT = 30;

    private final Random random = new Random();

    @Override
    public String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather) {
        double t = weather.temperature();
        int h = weather.humidity();

        if (t >= HOT_TEMP && h >= HUMID_PCT) {
            return spawnNearPlayer(map, location, chooseCreature(t, h));
        }

        if (t <= COLD_TEMP && h <= DRY_PCT) {
            return despawnHostile(map);
        }

        return "Spawn rates unaffected by current weather.";
    }

    private Actor chooseCreature(double temp, int humidity) {
        if (humidity >= 80) return new Slime();
        if (temp >= 35) return new Undead();
        return new Parasite();
    }

    private String spawnNearPlayer(GameMap map, Location playerLocation, Actor creature) {
        List<Location> valid = new ArrayList<>();
        for (Exit exit : playerLocation.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(creature)) valid.add(dest);
        }
        if (valid.isEmpty()) return "No space nearby for a creature to spawn.";

        Location spot = valid.get(random.nextInt(valid.size()));
        try {
            spot.addActor(creature);
            SpawnEffectManager.getInstance().onCreatureSpawned(creature, spot);
            return creature + " spawned by the weather conditions near " + playerLocation + "!";
        } catch (GameEngineException e) {
            return "Failed to spawn creature.";
        }
    }

    private String despawnHostile(GameMap map) {
        List<Actor> removable = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()
                        && loc.getActor().hasAbility(Ability.HOSTILE_TO_WORKERS)) {
                    removable.add(loc.getActor());
                }
            }
        }
        if (removable.isEmpty()) return "No hostile creatures to despawn.";

        Actor target = removable.get(random.nextInt(removable.size()));
        map.removeActor(target);
        return "Harsh weather claims " + target + " — it freezes in the cold.";
    }
}
