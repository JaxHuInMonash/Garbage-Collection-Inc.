package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * A {@link WeatherEffect} that inflicts environmental damage or grants healing
 * to all actors on the map based on the current temperature.
 *
 * <ul>
 *   <li>Temperature &le; 5°C (normalised &le; 0.20) → 1 cold damage to all actors</li>
 *   <li>Temperature 15–25°C (normalised 0.40–0.60) → heal all conscious actors by 1</li>
 *   <li>Temperature &ge; 30°C (normalised &ge; 0.70) → 2 heat damage to all actors</li>
 * </ul>
 */
public class HealthEffect implements WeatherEffect {

    private static final double COLD_THRESHOLD = 0.20;
    private static final double MILD_LOWER = 0.40;
    private static final double MILD_UPPER = 0.60;
    private static final double HEAT_THRESHOLD = 0.70;

    private static final int COLD_DAMAGE = 1;
    private static final int HEAT_DAMAGE = 2;
    private static final int MILD_HEAL = 1;

    @Override
    public String apply(Actor actor, GameMap map, Location location, List<WeatherDataPoint> dataPoints) {
        TemperatureDataPoint temp = findDataPoint(dataPoints);
        if (temp == null) {
            return "";
        }

        double normalised = temp.getNormalisedValue();
        StringBuilder sb = new StringBuilder();

        if (normalised <= COLD_THRESHOLD) {
            sb.append("Freezing cold: ");
            sb.append(applyDamageToAll(map, COLD_DAMAGE, "shivers from the cold"));
        } else if (normalised >= HEAT_THRESHOLD) {
            sb.append("Scorching heat: ");
            sb.append(applyDamageToAll(map, HEAT_DAMAGE, "suffers from heat stroke"));
        } else if (normalised >= MILD_LOWER && normalised <= MILD_UPPER) {
            sb.append("Mild temperature: ");
            sb.append(healAll(map));
        } else {
            sb.append("Temperature is tolerable — no health effects.");
        }

        return sb.toString();
    }

    private String applyDamageToAll(GameMap map, int damage, String verb) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    int before = target.getStatistic(ActorStatistics.HEALTH);
                    target.hurt(damage);
                    int after = target.getStatistic(ActorStatistics.HEALTH);
                    if (after < before) {
                        affected++;
                    }
                }
            }
        }
        return affected + " actor(s) " + verb + " for " + damage + " HP.";
    }

    private String healAll(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    int before = target.getStatistic(ActorStatistics.HEALTH);
                    target.heal(MILD_HEAL);
                    int after = target.getStatistic(ActorStatistics.HEALTH);
                    if (after > before) {
                        affected++;
                    }
                }
            }
        }
        return affected + " actor(s) healed by " + MILD_HEAL + " HP.";
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
