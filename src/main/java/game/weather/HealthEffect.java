package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.statuses.BurnStatus;
import game.statuses.DizzyStatus;

import java.util.List;

/**
 * A {@link WeatherEffect} that applies temperature-driven status effects across
 * all actors on the map, integrating with the existing status system.
 *
 * <ul>
 *   <li>Temperature &le; 5°C (normalised &le; 0.20) → applies {@link DizzyStatus}
 *       (skip 1 turn) to all unprotected workers</li>
 *   <li>Temperature 15–25°C (normalised 0.40–0.60) → heals all actors by 1 HP</li>
 *   <li>Temperature &ge; 30°C (normalised &ge; 0.70) → applies {@link BurnStatus}
 *       (1 dmg/turn for 3 turns) to all unprotected workers</li>
 * </ul>
 *
 * Workers carrying a SterilisationBox ({@link Ability#HAS_STERILISATION}) are
 * protected from temperature extremes, reusing the existing game mechanic.
 */
public class HealthEffect implements WeatherEffect {

    private static final double COLD_THRESHOLD = 0.20;
    private static final double MILD_LOWER = 0.40;
    private static final double MILD_UPPER = 0.60;
    private static final double HEAT_THRESHOLD = 0.70;

    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 3;
    private static final int COLD_STUN_TURNS = 1;
    private static final int MILD_HEAL = 1;

    @Override
    public String apply(Actor actor, GameMap map, Location location, List<WeatherDataPoint> dataPoints) {
        TemperatureDataPoint temp = findDataPoint(dataPoints);
        if (temp == null) {
            return "";
        }

        double normalised = temp.getNormalisedValue();

        if (normalised <= COLD_THRESHOLD) {
            return "Freezing cold: " + applyColdToAll(map);
        } else if (normalised >= HEAT_THRESHOLD) {
            return "Scorching heat: " + applyHeatToAll(map);
        } else if (normalised >= MILD_LOWER && normalised <= MILD_UPPER) {
            return "Mild temperature: " + healAll(map);
        }

        return "Temperature is tolerable — no health effects.";
    }

    /**
     * Applies DizzyStatus to unprotected workers. Workers with HAS_STERILISATION
     * are shielded from the cold.
     */
    private String applyColdToAll(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.HAS_STERILISATION)) {
                        continue; // sterilisation box protects from cold
                    }
                    target.addStatus(new DizzyStatus(COLD_STUN_TURNS));
                    affected++;
                }
            }
        }
        return affected + " actor(s) stunned by the cold (skip 1 turn).";
    }

    /**
     * Applies BurnStatus to unprotected workers. Workers with HAS_STERILISATION
     * are shielded from the heat.
     */
    private String applyHeatToAll(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.HAS_STERILISATION)) {
                        continue; // sterilisation box protects from heat
                    }
                    target.addStatus(new BurnStatus(BURN_DAMAGE, BURN_DURATION));
                    affected++;
                }
            }
        }
        return affected + " actor(s) afflicted by heat (Burn " + BURN_DAMAGE
                + " dmg/turn for " + BURN_DURATION + " turns).";
    }

    private String healAll(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    target.heal(MILD_HEAL);
                    affected++;
                }
            }
        }
        return affected + " actor(s) healed by " + MILD_HEAL + " HP (comfortable weather).";
    }

    private TemperatureDataPoint findDataPoint(List<WeatherDataPoint> points) {
        for (WeatherDataPoint dp : points) {
            if (dp instanceof TemperatureDataPoint) {
                return (TemperatureDataPoint) dp;
            }
        }
        return null;
    }
}
