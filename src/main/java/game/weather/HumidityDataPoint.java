package game.weather;

/**
 * A {@link WeatherDataPoint} that extracts and normalises the current
 * humidity percentage from the weather API response.
 *
 * Humidity range: 0% (bone dry) → 0.0, 100% (saturated) → 1.0.
 */
public class HumidityDataPoint implements WeatherDataPoint {

    private int rawValue;
    private double normalised;

    @Override
    public void readFrom(WeatherApiResponse response) {
        this.rawValue = response.getHumidity();
        this.normalised = rawValue / 100.0;
    }

    @Override
    public double getNormalisedValue() {
        return normalised;
    }

    @Override
    public String getLabel() {
        return String.format("Humidity: %d%% (%.2f)", rawValue, normalised);
    }

    /**
     * Returns the raw humidity percentage.
     *
     * @return raw humidity (0-100)
     */
    public int getRawValue() {
        return rawValue;
    }
}
