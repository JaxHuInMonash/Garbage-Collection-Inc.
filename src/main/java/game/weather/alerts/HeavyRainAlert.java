package game.weather.alerts;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.capabilities.WeatherAffected;
import game.weather.WeatherApiResponse;

/**
 * A {@link WeatherAlert} triggered by heavy rain (humidity &ge; 85% and the
 * weather condition has the {@link Ability#EXTINGUISH} ability).
 *
 * <p>The actual extinguishing of fire tiles is handled by {@link game.grounds.Fire}
 * through the {@link WeatherAffected} interface — this alert only reports the event.
 */
public class HeavyRainAlert implements WeatherAlert {

    private static final int HUMIDITY_THRESHOLD = 85;

    @Override
    public boolean isTriggered(WeatherApiResponse weather) {
        return weather.humidity() >= HUMIDITY_THRESHOLD
                && weather.condition().hasAbility(Ability.EXTINGUISH);
    }

    @Override
    public String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather) {
        return "Heavy rain extinguishes fires across the facility.";
    }
}
