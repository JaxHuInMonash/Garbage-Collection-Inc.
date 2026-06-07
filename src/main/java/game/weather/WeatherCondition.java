package game.weather;

import game.capabilities.Ability;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Weather condition types from the OpenWeatherMap API.
 *
 * <p>Each constant carries its semantic {@link Ability abilities} declared at
 * construction time — consumers use {@link #hasAbility(Ability)} instead of
 * matching raw strings, eliminating connascence of meaning.
 *
 * <p>New conditions can be added without modifying any logic, and each
 * condition declares its own effects.
 */
public enum WeatherCondition {

    // ── Conditions that can extinguish fires ──
    RAIN(Ability.EXTINGUISH),
    DRIZZLE(Ability.EXTINGUISH),
    THUNDERSTORM(Ability.EXTINGUISH),

    // ── Other conditions (no weather abilities) ──
    CLEAR,
    CLOUDS,
    SNOW,
    MIST,
    FOG,
    HAZE,
    DUST,
    SAND,
    SMOKE,
    TORNADO,
    SQUALL;

    private final Set<Ability> abilities;

    WeatherCondition(Ability... abilities) {
        Set<Ability> s = EnumSet.noneOf(Ability.class);
        Collections.addAll(s, abilities);
        this.abilities = Collections.unmodifiableSet(s);
    }

    /**
     * Returns true if this condition carries the given semantic ability.
     *
     * @param ability the ability to check
     * @return true if this condition has the ability
     */
    public boolean hasAbility(Ability ability) {
        return abilities.contains(ability);
    }

    /**
     * Maps an API response string to the corresponding condition.
     *
     * @param label the raw condition label from the API (e.g. "Rain", "Clear")
     * @return the matching enum constant, or {@link #CLEAR} for unknown values
     */
    public static WeatherCondition fromApiString(String label) {
        if (label == null || label.isBlank()) return CLEAR;
        try {
            return valueOf(label.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CLEAR;
        }
    }

    /**
     * Returns a human-readable label for display.
     *
     * @return e.g. "Rain", "Thunderstorm", "Clear"
     */
    @Override
    public String toString() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
