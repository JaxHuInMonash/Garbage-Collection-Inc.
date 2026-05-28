package game.weather;

/**
 * Immutable container for parsed weather API response data.
 * Produced by {@link WeatherApiClient} after deserialising the JSON response
 * from OpenWeatherMap. Each {@link WeatherDataPoint} reads the field it needs
 * from this record.
 */
public class WeatherApiResponse {

    private final double temperature;
    private final int humidity;
    private final double windSpeed;
    private final String condition;

    /**
     * Constructor.
     *
     * @param temperature temperature in Celsius
     * @param humidity    humidity percentage (0-100)
     * @param windSpeed   wind speed in metres per second
     * @param condition   weather condition label (e.g. "Clear", "Rain")
     */
    public WeatherApiResponse(double temperature, int humidity, double windSpeed, String condition) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
    }

    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public String getCondition() { return condition; }
}
