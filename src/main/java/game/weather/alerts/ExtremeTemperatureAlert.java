package game.weather.alerts;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.statuses.BurnStatus;
import game.statuses.DizzyStatus;
import game.weather.WeatherApiResponse;
import game.weather.effect.HealthEffect;

/**
 * A {@link WeatherAlert} for temperature extremes beyond the normal thresholds
 * handled by {@link HealthEffect}.
 *
 * <ul>
 *   <li>Temperature &lt; -5°C → applies DizzyStatus for 3 turns (severe cold)</li>
 *   <li>Temperature &gt; 40°C → applies BurnStatus for 5 turns (severe heat)</li>
 * </ul>
 *
 * SterilisationBox ({@link Ability#HAS_STERILISATION}) grants immunity.
 * Thresholds are intentionally more extreme than HealthEffect's.
 */
public class ExtremeTemperatureAlert implements WeatherAlert {

    private static final double EXTREME_COLD = -5.0;
    private static final double EXTREME_HEAT = 40.0;

    private static final int SEVERE_COLD_STUN = 3;
    private static final int SEVERE_BURN_DAMAGE = 2;
    private static final int SEVERE_BURN_DURATION = 5;

    @Override
    public boolean isTriggered(WeatherApiResponse weather) {
        return weather.temperature() < EXTREME_COLD || weather.temperature() > EXTREME_HEAT;
    }

    @Override
    public String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather) {
        if (weather.temperature() < EXTREME_COLD) {
            return applySevereCold(map);
        }
        return applySevereHeat(map);
    }

    private String applySevereCold(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                if (!map.at(x, y).containsAnActor()) continue;
                Actor target = map.at(x, y).getActor();
                if (target.hasAbility(Ability.HAS_STERILISATION)) continue;
                target.addStatus(new DizzyStatus(SEVERE_COLD_STUN));
                affected++;
            }
        }
        return affected + " actor(s) severely frostbitten (Dizzy " + SEVERE_COLD_STUN + " turns).";
    }

    private String applySevereHeat(GameMap map) {
        int affected = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                if (!map.at(x, y).containsAnActor()) continue;
                Actor target = map.at(x, y).getActor();
                if (target.hasAbility(Ability.HAS_STERILISATION)) continue;
                target.addStatus(new BurnStatus(SEVERE_BURN_DAMAGE, SEVERE_BURN_DURATION));
                affected++;
            }
        }
        return affected + " actor(s) suffering severe heat (Burn " + SEVERE_BURN_DAMAGE
                + " dmg/turn for " + SEVERE_BURN_DURATION + " turns).";
    }
}
