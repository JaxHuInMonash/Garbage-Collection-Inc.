package game.weather.effect;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.capabilities.WeatherAffected;
import game.weather.WeatherApiResponse;
import game.weather.WeatherCondition;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Smoke tests for {@link TerrainEffect}: verifies the dispatch loop calls
 * {@link WeatherAffected#onWeatherChange} on every WeatherAffected tile.
 */
class TerrainEffectTest {

    @Test
    void dispatchesOnWeatherChangeToEachAffectedTile() {
        GameMap map = mock(GameMap.class);
        Location reactiveLocation = mock(Location.class);
        WeatherAffected reactiveGround = mock(WeatherAffected.class);

        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(reactiveLocation);
        when(reactiveLocation.getGroundAs(WeatherAffected.class)).thenReturn(reactiveGround);
        when(reactiveGround.onWeatherChange(eq(reactiveLocation), any())).thenReturn(true);

        WeatherApiResponse weather = new WeatherApiResponse(15.0, 90, 1.0, WeatherCondition.RAIN);

        new TerrainEffect().apply(null, map, reactiveLocation, weather);

        verify(reactiveGround).onWeatherChange(reactiveLocation, weather);
    }

    @Test
    void skipsTilesWithoutWeatherAffectedGround() {
        GameMap map = mock(GameMap.class);
        Location nonReactiveLocation = mock(Location.class);

        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(nonReactiveLocation);
        when(nonReactiveLocation.getGroundAs(WeatherAffected.class)).thenReturn(null);

        WeatherApiResponse weather = new WeatherApiResponse(15.0, 50, 1.0, WeatherCondition.CLEAR);

        String result = new TerrainEffect().apply(null, map, nonReactiveLocation, weather);

        // Confirms the loop completed without invoking anything (null-safe path).
        verify(nonReactiveLocation).getGroundAs(WeatherAffected.class);
        org.junit.jupiter.api.Assertions.assertEquals(
                "No weather-reactive terrain effected.", result);
    }
}
