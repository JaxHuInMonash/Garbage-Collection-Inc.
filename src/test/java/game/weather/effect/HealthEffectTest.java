package game.weather.effect;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.TestActor;
import game.statuses.BurnStatus;
import game.statuses.DizzyStatus;
import game.weather.WeatherApiResponse;
import game.weather.WeatherCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Smoke tests for {@link HealthEffect}: temperature branches and the
 * map-wide application of status effects to actors.
 */
class HealthEffectTest {

    private GameMap map;
    private Location location;
    private TestActor actor;

    @BeforeEach
    void setUp() {
        map = mock(GameMap.class);
        location = mock(Location.class);
        actor = new TestActor();

        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(location);
        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(actor);
    }

    @Test
    void appliesDizzyStatusInFreezingCold() {
        WeatherApiResponse weather = new WeatherApiResponse(-2.0, 50, 1.0, WeatherCondition.CLEAR);

        String result = new HealthEffect().apply(actor, map, location, weather);

        assertTrue(actor.hasStatus(DizzyStatus.class), "actor should be dizzy");
        assertTrue(result.startsWith("Freezing cold"), "unexpected: " + result);
    }

    @Test
    void appliesBurnStatusInScorchingHeat() {
        WeatherApiResponse weather = new WeatherApiResponse(35.0, 20, 1.0, WeatherCondition.CLEAR);

        String result = new HealthEffect().apply(actor, map, location, weather);

        assertTrue(actor.hasStatus(BurnStatus.class));
        assertTrue(result.startsWith("Scorching heat"), "unexpected: " + result);
    }

    @Test
    void healsActorInMildWeather() {
        actor.hurt(5); // bring HP below max so we can detect healing

        WeatherApiResponse weather = new WeatherApiResponse(20.0, 50, 1.0, WeatherCondition.CLEAR);
        String result = new HealthEffect().apply(actor, map, location, weather);

        assertFalse(actor.hasStatus(BurnStatus.class));
        assertFalse(actor.hasStatus(DizzyStatus.class));
        assertTrue(result.startsWith("Mild temperature"), "unexpected: " + result);
    }

    @Test
    void tolerableTemperatureProducesNoEffect() {
        WeatherApiResponse weather = new WeatherApiResponse(10.0, 50, 1.0, WeatherCondition.CLEAR);

        String result = new HealthEffect().apply(actor, map, location, weather);

        assertFalse(actor.hasStatus(BurnStatus.class));
        assertFalse(actor.hasStatus(DizzyStatus.class));
        assertEquals("Temperature is tolerable — no health effects.", result);
    }
}
