package game.weather.alerts;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.grounds.Fire;
import game.weather.WeatherApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A {@link WeatherAlert} triggered by high wind speeds (&gt; 20 m/s).
 * Any existing {@link Fire} tile on the map spreads to one adjacent
 * empty tile — wind fans the flames across the facility.
 */
public class HighWindAlert implements WeatherAlert {

    private static final double WIND_THRESHOLD = 20.0;

    private final Random random = new Random();

    @Override
    public boolean isTriggered(WeatherApiResponse weather) {
        return weather.windSpeed() > WIND_THRESHOLD;
    }

    @Override
    public String apply(Actor actor, GameMap map, Location location, WeatherApiResponse weather) {
        List<Location> fireLocations = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround().hasAbility(Ability.ON_FIRE)) {
                    fireLocations.add(loc);
                }
            }
        }

        if (fireLocations.isEmpty()) {
            return "High winds detected, but no fires to spread.";
        }

        int spread = 0;
        for (Location fireLoc : fireLocations) {
            if (trySpreadFire(fireLoc)) spread++;
        }

        return "High wind! " + spread + " fire(s) spread by the wind.";
    }

    private boolean trySpreadFire(Location fireLocation) {
        List<Location> candidates = new ArrayList<>();
        for (Exit exit : fireLocation.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.getGround().hasAbility(Ability.ON_FIRE)
                    && !dest.getGround().hasAbility(Ability.BLOCKS_ENTRY)) {
                candidates.add(dest);
            }
        }
        if (candidates.isEmpty()) return false;

        Location target = candidates.get(random.nextInt(candidates.size()));
        target.setGround(new Fire(target.getGround(), 3));
        return true;
    }
}
