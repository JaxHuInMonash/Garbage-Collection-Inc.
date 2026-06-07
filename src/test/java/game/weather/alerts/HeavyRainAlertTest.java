package game.weather.alerts;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.capabilities.Ability;
import game.weather.WeatherApiResponse;
import game.weather.WeatherCondition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Smoke tests for {@link HeavyRainAlert}: trigger threshold and apply().
 */
class HeavyRainAlertTest {

    @Test
    void triggersOnRainWithHighHumidity() {
        HeavyRainAlert alert = new HeavyRainAlert();
        WeatherApiResponse w = new WeatherApiResponse(15.0, 90, 1.0, WeatherCondition.RAIN);
        assertTrue(alert.isTriggered(w));
    }

    @Test
    void doesNotTriggerOnClearSkyEvenWithHighHumidity() {
        HeavyRainAlert alert = new HeavyRainAlert();
        WeatherApiResponse w = new WeatherApiResponse(15.0, 90, 1.0, WeatherCondition.CLEAR);
        assertFalse(alert.isTriggered(w));
    }

    @Test
    void doesNotTriggerOnLowHumidityRain() {
        HeavyRainAlert alert = new HeavyRainAlert();
        WeatherApiResponse w = new WeatherApiResponse(15.0, 50, 1.0, WeatherCondition.RAIN);
        assertFalse(alert.isTriggered(w));
    }

    @Test
    void applyReportsNoFiresWhenMapIsClear() {
        GameMap map = mock(GameMap.class);
        Location loc = mock(Location.class);
        Ground ground = mock(Ground.class);
        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(loc);
        when(loc.getGround()).thenReturn(ground);
        when(ground.hasAbility(Ability.ON_FIRE)).thenReturn(false);

        WeatherApiResponse w = new WeatherApiResponse(15.0, 90, 1.0, WeatherCondition.RAIN);
        String result = new HeavyRainAlert().apply(null, map, loc, w);

        assertEquals("Heavy rain had no fires to extinguish.", result);
    }
}
