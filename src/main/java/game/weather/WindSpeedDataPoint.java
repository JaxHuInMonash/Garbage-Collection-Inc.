package game.weather;

/**
 * A {@link WeatherDataPoint} that extracts and normalises the current
 * wind speed from the weather API response.
 *
 * Wind speed range: 0 m/s (calm) → 0.0, 50 m/s (hurricane-force) → 1.0.
 * Values above 50 m/s are clamped to 1.0.
 */
public class WindSpeedDataPoint implements WeatherDataPoint {

    private static final double MAX_WIND = 50.0;

    private double rawValue;
    private double normalised;

    @Override
    public void readFrom(WeatherApiResponse response) {
        this.rawValue = response.windSpeed();
        this.normalised = Math.min(rawValue / MAX_WIND, 1.0);
    }

    @Override
    public double getNormalisedValue() {
        return normalised;
    }

    @Override
    public String getLabel() {
        return String.format("Wind Speed: %.1f m/s (%.2f)", rawValue, normalised);
    }

    /**
     * Returns the raw wind speed in metres per second.
     *
     * @return raw wind speed
     */
    public double getRawValue() {
        return rawValue;
    }
}
