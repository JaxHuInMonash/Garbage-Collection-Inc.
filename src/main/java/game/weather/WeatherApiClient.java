package game.weather;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Makes HTTP requests to the OpenWeatherMap API and parses the JSON response
 * into a {@link WeatherApiResponse}.
 *
 * <p>The API key is read from the {@code OPENWEATHER_API_KEY} environment
 * variable. JSON parsing is done manually (no external dependencies) since
 * the response structure is known and stable.
 */
public class WeatherApiClient {

    private static final String API_KEY_VAR = "OPENWEATHER_API_KEY";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    // Fallback values when the API is unreachable
    private static final double FALLBACK_TEMPERATURE = 20.0;
    private static final int FALLBACK_HUMIDITY = 50;
    private static final double FALLBACK_WIND_SPEED = 2.0;

    // The "main" key appears at JSON root (temp/humidity) and again
    // inside weather[0] (condition label); this skips past the first
    // occurrence to read the weather condition.
    private static final int WEATHER_CONDITION_KEY_OCCURRENCE = 2;

    private final HttpClient httpClient;

    /** Constructor. */
    public WeatherApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Fetches current weather data for the given coordinates.
     *
     * @param lat latitude (driven by player's y-coordinate)
     * @param lon longitude (driven by player's x-coordinate)
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
     * Manually parses the OpenWeatherMap JSON response.
     *
     * The response has a known structure, so we extract values by searching
     * for known key strings rather than using a full JSON parser. This avoids
     * any external dependency.
     *
     * Expected JSON fragment:
     * <pre>
     * { "main": { "temp": 18.5, "humidity": 65 },
     *   "wind": { "speed": 4.2 },
     *   "weather": [ { "main": "Rain" } ] }
     * </pre>
     */
    private WeatherApiResponse parseJson(String json) {
        double temp = extractDouble(json, "\"temp\"");
        int humidity = extractInt(json, "\"humidity\"");
        double windSpeed = extractDouble(json, "\"speed\"");
        String rawCondition = extractString(json, "\"main\"", WEATHER_CONDITION_KEY_OCCURRENCE);
        WeatherCondition condition = WeatherCondition.fromApiString(rawCondition);

        return new WeatherApiResponse(temp, humidity, windSpeed, condition);
    }

    /**
     * Extracts a double value following the first occurrence of {@code key}.
     * Expects the value to appear as {@code "key": <number>}.
     */
    private double extractDouble(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) return 0;

        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex < 0) return 0;

        int start = skipWhitespace(json, colonIndex + 1);
        int end = start;
        while (end < json.length() && (isDigitOrDot(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }

        try {
            return Double.parseDouble(json.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Extracts an integer value following the first occurrence of {@code key}.
     */
    private int extractInt(String json, String key) {
        return (int) extractDouble(json, key);
    }

    /**
     * Extracts a quoted string value following the {@code skipNth}-th
     * occurrence of {@code key}. This handles the case where the key appears
     * multiple times (e.g. "main" appears in both the root and nested objects).
     */
    private String extractString(String json, String key, int skipNth) {
        int keyIndex = -1;
        for (int i = 0; i < skipNth; i++) {
            keyIndex = json.indexOf(key, keyIndex + 1);
            if (keyIndex < 0) return "Unknown";
        }

        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex < 0) return "Unknown";

        int start = skipWhitespace(json, colonIndex + 1);
        // Expect opening quote
        if (start >= json.length() || json.charAt(start) != '"') return "Unknown";
        start++;

        int end = start;
        while (end < json.length() && json.charAt(end) != '"') {
            if (json.charAt(end) == '\\') end++; // skip escaped chars
            end++;
        }

        return json.substring(start, end);
    }

    private int skipWhitespace(String json, int from) {
        int i = from;
        while (i < json.length() && json.charAt(i) <= ' ') i++;
        return i;
    }

    private boolean isDigitOrDot(char c) {
        return Character.isDigit(c) || c == '.';
    }

    /**
     * Returns a safe fallback response when the API cannot be reached.
     * Uses mild, neutral weather so the game remains playable.
     */
    private WeatherApiResponse fallback(String reason) {
        System.err.println("[WeatherApiClient] " + reason + " Using fallback data.");
        return new WeatherApiResponse(FALLBACK_TEMPERATURE, FALLBACK_HUMIDITY, FALLBACK_WIND_SPEED, WeatherCondition.CLEAR);
    }
}
