package game.grounds;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.weather.WeatherApiResponse;
import game.weather.WeatherCondition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Smoke tests for the three {@link game.capabilities.WeatherAffected} grounds:
 * Dirt floods, Puddle evaporates, Fire is extinguished by heavy rain.
 */
class WeatherAffectedGroundsTest {

    // ── Dirt ──────────────────────────────────────────────────────────────

    @Test
    void dirtFloodsInHighHumidityWhenAdjacentToWater() {
        Location dirtLoc = mock(Location.class);
        Location waterLoc = mock(Location.class);
        Exit exit = mock(Exit.class);
        Ground waterGround = mock(Ground.class);

        when(dirtLoc.getExits()).thenReturn(List.of(exit));
        when(exit.getDestination()).thenReturn(waterLoc);
        when(waterLoc.getGround()).thenReturn(waterGround);
        when(waterGround.hasAbility(Ability.EVAPORATABLE)).thenReturn(true);

        WeatherApiResponse humid =
                new WeatherApiResponse(15.0, 80, 1.0, WeatherCondition.RAIN);

        boolean changed = new Dirt().onWeatherChange(dirtLoc, humid);

        assertTrue(changed);
        verify(dirtLoc).setGround(any(Puddle.class));
    }

    @Test
    void dirtDoesNotFloodInLowHumidity() {
        Location dirtLoc = mock(Location.class);
        when(dirtLoc.getExits()).thenReturn(List.of());

        WeatherApiResponse dry =
                new WeatherApiResponse(15.0, 40, 1.0, WeatherCondition.CLEAR);

        boolean changed = new Dirt().onWeatherChange(dirtLoc, dry);

        assertFalse(changed);
        verify(dirtLoc, never()).setGround(any());
    }

    // ── Puddle ────────────────────────────────────────────────────────────

    @Test
    void puddleEvaporatesInLowHumidity() {
        Location loc = mock(Location.class);

        WeatherApiResponse dry =
                new WeatherApiResponse(25.0, 20, 1.0, WeatherCondition.CLEAR);

        boolean changed = new Puddle().onWeatherChange(loc, dry);

        assertTrue(changed);
        verify(loc).setGround(any(Dirt.class));
    }

    @Test
    void puddleDoesNotEvaporateInHighHumidity() {
        Location loc = mock(Location.class);

        WeatherApiResponse humid =
                new WeatherApiResponse(15.0, 80, 1.0, WeatherCondition.RAIN);

        boolean changed = new Puddle().onWeatherChange(loc, humid);

        assertFalse(changed);
        verify(loc, never()).setGround(any());
    }

    // ── Fire ──────────────────────────────────────────────────────────────

    @Test
    void fireRevertsToOriginalGroundInHeavyRain() {
        Location loc = mock(Location.class);
        Ground originalGround = mock(Ground.class);
        Fire fire = new Fire(originalGround);

        WeatherApiResponse heavyRain =
                new WeatherApiResponse(15.0, 90, 1.0, WeatherCondition.RAIN);

        boolean changed = fire.onWeatherChange(loc, heavyRain);

        assertTrue(changed);
        verify(loc).setGround(originalGround);
    }

    @Test
    void fireDoesNotExtinguishWithoutExtinguishCondition() {
        Location loc = mock(Location.class);
        Ground originalGround = mock(Ground.class);
        Fire fire = new Fire(originalGround);

        // High humidity but condition is CLEAR (no EXTINGUISH ability)
        WeatherApiResponse muggyButDry =
                new WeatherApiResponse(15.0, 95, 1.0, WeatherCondition.CLEAR);

        boolean changed = fire.onWeatherChange(loc, muggyButDry);

        assertFalse(changed);
        verify(loc, never()).setGround(any());
    }
}
