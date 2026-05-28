package game.weather;

/**
 * Parsed weather API response data.
 * Produced by {@link WeatherApiClient} after deserialising the JSON response
 * from OpenWeatherMap. Each {@link WeatherDataPoint} reads the field it needs
 * from this record.
 *
 * @param temperature temperature in Celsius
 * @param humidity    humidity percentage (0–100)
 * @param windSpeed   wind speed in metres per second
 * @param condition   weather condition label (e.g. "Clear", "Rain")
 */
public record WeatherApiResponse(double temperature, int humidity, double windSpeed, String condition) {
}
