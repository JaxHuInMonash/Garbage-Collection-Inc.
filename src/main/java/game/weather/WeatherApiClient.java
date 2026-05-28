package game.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Makes HTTP requests to the OpenWeatherMap API and parses the JSON response
 * into a {@link WeatherApiResponse}.
 *
 * <p>The API key is read from the {@code OPENWEATHER_API_KEY} environment
 * variable. The request URL uses the supplied latitude and longitude and
 * requests metric units (Celsius, m/s wind).
 */
public class WeatherApiClient {

    private static final String API_KEY_VAR = "OPENWEATHER_API_KEY";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final HttpClient httpClient;

    /** Constructor. */
    public WeatherApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Fetches current weather data for the given coordinates.
     *
     * @param lat latitude
     * @param lon longitude
     * @return parsed weather data, or a fallback response if the API call fails
     */
    public WeatherApiResponse fetch(double lat, double lon) {
        String apiKey = System.getenv(API_KEY_VAR);
        if (apiKey == null || apiKey.isBlank()) {
            return fallback("API key not set — using fallback data.");
        }

        String url = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return fallback("API returned status " + response.statusCode());
            }

            return parseJson(response.body());
        } catch (Exception e) {
            return fallback("API call failed: " + e.getMessage());
        }
    }

    /**
     * Parses the OpenWeatherMap JSON response into a {@link WeatherApiResponse}.
     */
    private WeatherApiResponse parseJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        JsonObject main = root.getAsJsonObject("main");
        double temp = main.get("temp").getAsDouble();
        int humidity = main.get("humidity").getAsInt();

        JsonObject wind = root.getAsJsonObject("wind");
        double windSpeed = wind.get("speed").getAsDouble();

        String condition = "Unknown";
        if (root.has("weather")) {
            JsonArray weather = root.getAsJsonArray("weather");
            if (weather.size() > 0) {
                condition = weather.get(0).getAsJsonObject().get("main").getAsString();
            }
        }

        return new WeatherApiResponse(temp, humidity, windSpeed, condition);
    }

    /**
     * Returns a safe fallback response when the API cannot be reached.
     * Uses mild, neutral weather so the game remains playable.
     */
    private WeatherApiResponse fallback(String reason) {
        System.err.println("[WeatherApiClient] " + reason + " Using fallback data.");
        return new WeatherApiResponse(20.0, 50, 2.0, "Clear");
    }
}
