package game.weather;

/**
 * Abstraction representing a single normalised data field extracted from the
 * weather API response.
 *
 * Each concrete implementation reads one specific field from a
 * {@link WeatherApiResponse} via {@link #readFrom(WeatherApiResponse)} and
 * normalises it to a 0.0–1.0 range.
 *
 * Concrete implementations: {@link TemperatureDataPoint},
 * {@link HumidityDataPoint}, {@link WindSpeedDataPoint}.
 */
public interface WeatherDataPoint {

    /**
     * Populates this data point from the raw API response.
     *
     * @param response the parsed API response
     */
    void readFrom(WeatherApiResponse response);

    /**
     * Returns the normalised value in the range 0.0–1.0.
     *
     * @return normalised value
     */
    double getNormalisedValue();

    /**
     * Returns a human-readable label for this data point, including the
     * current raw value and the normalised interpretation.
     *
     * @return label string
     */
    String getLabel();
}
