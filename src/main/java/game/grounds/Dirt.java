package game.grounds;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.capabilities.WeatherAffected;
import game.weather.WeatherApiResponse;
import game.weather.effect.TerrainEffect;

/**
 * While other classes get to be security doors, mysterious flasks, or highly
 * stressed contracted workers, this class humbly accepts its role as
 * the thing everyone walks all over.
 *
 * @author Adrian Kristanto
 */
public class Dirt extends Ground implements WeatherAffected {

    private static final int HUMIDITY_FLOOD_THRESHOLD = 70;

    public Dirt() {
        super('.', "Dirt");
        this.enableAbility(Ability.FLOODABLE);
    }

    /**
     * In high humidity, Dirt adjacent to a water source floods into Puddle.
     * Called by {@link TerrainEffect} after a weather scan —
     * no polling in {@code tick()}.
     */
    @Override
    public void onWeatherChange(Location location, WeatherApiResponse weather) {
        if (weather.humidity() >= HUMIDITY_FLOOD_THRESHOLD && isAdjacentToWater(location)) {
            location.setGround(new Puddle());
        }
    }

    private boolean isAdjacentToWater(Location location) {
        for (Exit exit : location.getExits()) {
            if (exit.getDestination().getGround().hasAbility(Ability.EVAPORATABLE)) {
                return true;
            }
        }
        return false;
    }
}
