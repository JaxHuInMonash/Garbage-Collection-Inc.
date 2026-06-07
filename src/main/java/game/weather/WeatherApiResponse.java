package game.weather;

import game.weather.alerts.WeatherAlert;
import game.weather.effect.WeatherEffect;

/**
 * Parsed weather API response data.
 * Produced by {@link WeatherApiClient} after deserialising the JSON response
 * from OpenWeatherMap. Each {@link WeatherEffect} or {@link WeatherAlert}
 * reads the fields it needs directly from this record.
 *
 * @param temperature temperature in Celsius
 * @param humidity    humidity percentage (0–100)
 * @param windSpeed   wind speed in metres per second
 * @param condition   the weather condition with semantic abilities
 */
public record WeatherApiResponse(double temperature, int humidity, double windSpeed, WeatherCondition condition) {
}
