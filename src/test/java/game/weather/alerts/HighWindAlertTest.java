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
 * Smoke tests for {@link HighWindAlert}: trigger threshold and the
 * "no fires" branch of apply().
 */
class HighWindAlertTest {

    @Test
    void triggersAboveWindThreshold() {
        HighWindAlert alert = new HighWindAlert();
        WeatherApiResponse w = new WeatherApiResponse(15.0, 50, 25.0, WeatherCondition.CLEAR);
        assertTrue(alert.isTriggered(w));
    }

    @Test
    void doesNotTriggerAtOrBelowThreshold() {
        HighWindAlert alert = new HighWindAlert();
        WeatherApiResponse exactlyAtBoundary =
                new WeatherApiResponse(15.0, 50, 20.0, WeatherCondition.CLEAR);
        assertFalse(alert.isTriggered(exactlyAtBoundary));
    }

    @Test
    void reportsNothingToSpreadWhenMapIsFireFree() {
        GameMap map = mock(GameMap.class);
        Location loc = mock(Location.class);
        Ground ground = mock(Ground.class);
        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(loc);
        when(loc.getGround()).thenReturn(ground);
        when(ground.hasAbility(Ability.ON_FIRE)).thenReturn(false);

        WeatherApiResponse w = new WeatherApiResponse(15.0, 50, 25.0, WeatherCondition.CLEAR);
        String result = new HighWindAlert().apply(null, map, loc, w);

        assertEquals("High winds detected, but no fires to spread.", result);
    }
}
