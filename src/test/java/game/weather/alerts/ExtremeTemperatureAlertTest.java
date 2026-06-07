package game.weather.alerts;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.TestActor;
import game.statuses.BurnStatus;
import game.statuses.DizzyStatus;
import game.weather.WeatherApiResponse;
import game.weather.WeatherCondition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Smoke tests for {@link ExtremeTemperatureAlert}.
 */
class ExtremeTemperatureAlertTest {

    @Test
    void triggersBelowExtremeColdThreshold() {
        ExtremeTemperatureAlert alert = new ExtremeTemperatureAlert();
        assertTrue(alert.isTriggered(
                new WeatherApiResponse(-10.0, 50, 1.0, WeatherCondition.SNOW)));
    }

    @Test
    void triggersAboveExtremeHeatThreshold() {
        ExtremeTemperatureAlert alert = new ExtremeTemperatureAlert();
        assertTrue(alert.isTriggered(
                new WeatherApiResponse(45.0, 50, 1.0, WeatherCondition.CLEAR)));
    }

    @Test
    void doesNotTriggerInNormalRange() {
        ExtremeTemperatureAlert alert = new ExtremeTemperatureAlert();
        assertFalse(alert.isTriggered(
                new WeatherApiResponse(20.0, 50, 1.0, WeatherCondition.CLEAR)));
    }

    @Test
    void appliesSevereBurnInExtremeHeat() {
        GameMap map = mock(GameMap.class);
        Location loc = mock(Location.class);
        TestActor actor = new TestActor();
        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(loc);
        when(loc.containsAnActor()).thenReturn(true);
        when(loc.getActor()).thenReturn(actor);

        WeatherApiResponse w = new WeatherApiResponse(45.0, 50, 1.0, WeatherCondition.CLEAR);
        new ExtremeTemperatureAlert().apply(actor, map, loc, w);

        assertTrue(actor.hasStatus(BurnStatus.class));
    }

    @Test
    void appliesSevereDizzyInExtremeCold() {
        GameMap map = mock(GameMap.class);
        Location loc = mock(Location.class);
        TestActor actor = new TestActor();
        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(loc);
        when(loc.containsAnActor()).thenReturn(true);
        when(loc.getActor()).thenReturn(actor);

        WeatherApiResponse w = new WeatherApiResponse(-10.0, 50, 1.0, WeatherCondition.SNOW);
        new ExtremeTemperatureAlert().apply(actor, map, loc, w);

        assertTrue(actor.hasStatus(DizzyStatus.class));
    }
}
