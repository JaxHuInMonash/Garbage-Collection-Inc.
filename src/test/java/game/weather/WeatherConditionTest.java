package game.weather;

import game.capabilities.Ability;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for {@link WeatherCondition}: enum mapping and ability lookup.
 */
class WeatherConditionTest {

    @Test
    void mapsKnownLabelCaseInsensitively() {
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromApiString("Rain"));
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCondition.fromApiString("thunderstorm"));
    }

    @Test
    void mapsUnknownOrBlankToClear() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromApiString("AcidStorm"));
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromApiString(""));
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromApiString(null));
    }

    @Test
    void rainConditionsCarryExtinguishAbility() {
        assertTrue(WeatherCondition.RAIN.hasAbility(Ability.EXTINGUISH));
        assertTrue(WeatherCondition.DRIZZLE.hasAbility(Ability.EXTINGUISH));
        assertTrue(WeatherCondition.THUNDERSTORM.hasAbility(Ability.EXTINGUISH));
    }

    @Test
    void clearConditionHasNoExtinguishAbility() {
        assertFalse(WeatherCondition.CLEAR.hasAbility(Ability.EXTINGUISH));
        assertFalse(WeatherCondition.SNOW.hasAbility(Ability.EXTINGUISH));
    }
}
