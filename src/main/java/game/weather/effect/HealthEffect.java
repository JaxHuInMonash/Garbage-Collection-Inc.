package game.weather.effect;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.statuses.BurnStatus;
import game.statuses.DizzyStatus;
import game.weather.WeatherApiResponse;

/**
 * A {@link WeatherEffect} that applies temperature-driven status effects across
 * all actors on the map, integrating with the existing status system.
 *
 * <ul>
 *   <li>Temperature &le; 5°C → applies {@link DizzyStatus} (skip 1 turn)</li>
 *   <li>Temperature 15–25°C → heals all actors by 1 HP</li>
 *   <li>Temperature &ge; 30°C → applies {@link BurnStatus} (1 dmg/turn, 3 turns)</li>
 * </ul>
 *
 * Workers carrying a SterilisationBox ({@link Ability#HAS_STERILISATION}) are
 * protected from temperature extremes.
 */
public class HealthEffect implements WeatherEffect {

    private static final double COLD_THRESHOLD = 5.0;
    private static final double MILD_LOWER = 15.0;
    private static final double MILD_UPPER = 25.0;
    private static final double HEAT_THRESHOLD = 30.0;

    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 3;
    private static final int COLD_STUN_TURNS = 1;
    private static final int MILD_HEAL = 1;

    @Override
    public String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather) {
        double temp = weather.temperature();

        if (temp <= COLD_THRESHOLD) {
            return "Freezing cold: " + applyColdToAll(map);
        } else if (temp >= HEAT_THRESHOLD) {
            return "Scorching heat: " + applyHeatToAll(map);
        } else if (temp >= MILD_LOWER && temp <= MILD_UPPER) {
            return "Mild temperature: " + healAll(map);
        }

        return "Temperature is tolerable — no health effects.";
    }

    private String applyColdToAll(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                if (!map.at(x, y).containsAnActor()) continue;
                Actor target = map.at(x, y).getActor();
                if (target.hasAbility(Ability.HAS_STERILISATION)) continue;
                target.addStatus(new DizzyStatus(COLD_STUN_TURNS));
                affected++;
            }
        }
        return affected + " actor(s) stunned by the cold (skip 1 turn).";
    }

    private String applyHeatToAll(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                if (!map.at(x, y).containsAnActor()) continue;
                Actor target = map.at(x, y).getActor();
                if (target.hasAbility(Ability.HAS_STERILISATION)) continue;
                target.addStatus(new BurnStatus(BURN_DAMAGE, BURN_DURATION));
                affected++;
            }
        }
        return affected + " actor(s) afflicted by heat (Burn " + BURN_DAMAGE
                + " dmg/turn for " + BURN_DURATION + " turns).";
    }

    private String healAll(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                if (!map.at(x, y).containsAnActor()) continue;
                map.at(x, y).getActor().heal(MILD_HEAL);
                affected++;
            }
        }
        return affected + " actor(s) healed by " + MILD_HEAL + " HP (comfortable weather).";
    }
}
