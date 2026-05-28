package game.weather;

/**
 * A {@link WeatherDataPoint} that extracts and normalises the current
 * temperature from the weather API response.
 *
 * Temperature range: -5°C (freezing) → 0.0, 45°C (scorching) → 1.0.
 * Values outside this range are clamped to the nearest endpoint.
 */
public class TemperatureDataPoint implements WeatherDataPoint {

    private static final double MIN_TEMP = -5.0;
    private static final double MAX_TEMP = 45.0;

    private double rawValue;
    private double normalised;

    @Override
    public void readFrom(WeatherApiResponse response) {
        this.rawValue = response.getTemperature();
        this.normalised = clamp((rawValue - MIN_TEMP) / (MAX_TEMP - MIN_TEMP), 0.0, 1.0);
    }

    @Override
    public double getNormalisedValue() {
        return normalised;
    }

    @Override
    public String getLabel() {
        return String.format("Temperature: %.1f°C (%.2f)", rawValue, normalised);
    }

    /**
     * Returns the raw temperature in Celsius.
     *
     * @return raw temperature
     */
    public double getRawValue() {
        return rawValue;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
