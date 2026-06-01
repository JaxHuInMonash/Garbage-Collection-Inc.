# Feature Proposal — Assignment 3

*(REQ 5 ONLY)*

## REQ5 — External API Integration (HD)

**Owner:** Thomas
**API used:** OpenWeatherMap (`api.openweathermap.org`)

### 1. The Pitch

The game facility has an Environmental Monitoring Terminal that connects to
real-world weather data via the OpenWeatherMap API. The player's current map
coordinates are used as latitude/longitude in the API query — making the request
parameters dynamically driven by game state. The fetched weather data
(temperature, humidity, wind speed, condition) then drives three distinct
game-world effects (health, spawn rates, terrain changes) and three
threshold-based severe-weather alerts.

### 2. The Mechanics

- An **Environmental Terminal** (`¶`) is placed on both the 99-Deprecated and
  20-Overflow maps as a wall-mounted terminal. It cannot be entered but offers a
  **Weather Scan** action when the worker is standing on an adjacent tile.
- When activated, the **Weather Scan Action**:
  1. Reads the worker's current map position `(x, y)` and uses them as
     `(lon, lat)` for the OpenWeatherMap API query.
  2. Sends an HTTP GET to `https://api.openweathermap.org/data/2.5/weather`
     with the dynamically-determined lat/lon, metric units, and the API key
     from the `OPENWEATHER_API_KEY` environment variable.
  3. Parses the JSON response manually (no external JSON library) into a
     `WeatherApiResponse` record containing raw temperature (Celsius), humidity
     (0–100), wind speed (m/s), and a `WeatherCondition` enum.
  4. Applies three independent **effects** and checks three **alerts** against
     the current game map:

     **a. Health Effect** — Temperature-driven, uses existing status system:
     - ≤ 5°C: applies {@link game.statuses.DizzyStatus} (skip 1 turn) to all
       unprotected actors map-wide
     - 15–25°C: heals all actors by 1 HP (comfortable weather)
     - ≥ 30°C: applies {@link game.statuses.BurnStatus} (1 dmg/turn, 3 turns) to
       all unprotected actors map-wide
     - Workers with a SterilisationBox ({@link game.capabilities.Ability#HAS_STERILISATION})
       are protected from temperature extremes

     **b. Spawn Rate Effect** — Humidity + Temperature:
     - Hot (≥ 30°C) + Humid (≥ 60%): spawns a creature (Slime, Undead, or
       Parasite depending on exact conditions) adjacent to the worker; registered
       with the existing {@link game.spawning.SpawnEffectManager} for alarm
       system tracking
     - Cold (≤ 0°C) + Dry (≤ 30%): removes a random creature with
       {@link game.capabilities.Ability#HOSTILE_TO_WORKERS} from the map

     **c. Terrain Effect** — Dispatches weather data to all
        {@link game.capabilities.WeatherAffected} ground tiles on the map:
     - **Dirt** (implements `WeatherAffected`): in high humidity (≥ 70%) when
       adjacent to a Puddle, floods into a new Puddle
     - **Puddle** (implements `WeatherAffected`): in low humidity (≤ 30%),
       evaporates back into Dirt
     - **Fire** (implements `WeatherAffected`): in high humidity (≥ 85%) with a
       rain-type weather condition, reverts to its original ground tile
     - Tiles with {@link game.capabilities.Ability#BLOCKS_ENTRY} (walls, doors)
       are preserved
     - No concrete ground types are imported by `TerrainEffect` — tiles
       self-determine their response through the interface

     **d. Weather Alerts** (threshold-based severe-weather consequences):
     - **ExtremeTemperatureAlert** — Temp < −5°C or > 40°C: stronger DizzyStatus
       (3 turns) or BurnStatus (2 dmg/turn, 5 turns) beyond normal HealthEffect
     - **HighWindAlert** — Wind > 20 m/s: spreads existing Fire tiles to adjacent
       passable tiles (wind fans the flames)
     - **HeavyRainAlert** — Humidity ≥ 85% + rain-type condition: reports that
       fires are extinguished (actual extinguishing via Fire's `onWeatherChange`)
     - SterilisationBox (`HAS_STERILISATION`) grants immunity to temperature alerts

- If the API call fails (no network, missing key, etc.), a safe fallback
  response (20°C, 50% humidity, 2 m/s, Clear) is used so the game remains
  playable.

### 3. The Request

**Example URL sent to the API:**
```
https://api.openweathermap.org/data/2.5/weather?lat=11&lon=44&appid=YOUR_KEY&units=metric
```

**Dynamic parameters:**
- `lat` = player's current `y` map coordinate
- `lon` = player's current `x` map coordinate
- `appid` = read from `OPENWEATHER_API_KEY` environment variable
- `units` = always `metric`

### 4. The Schema

**Example JSON the game expects to receive back:**
```json
{
  "main": {
    "temp": 18.5,
    "humidity": 65
  },
  "wind": {
    "speed": 4.2
  },
  "weather": [
    {
      "main": "Rain",
      "description": "light rain"
    }
  ]
}
```

**Internal response type** — `WeatherApiResponse` is a Java record:

```java
public record WeatherApiResponse(
    double temperature,       // Celsius
    int humidity,             // 0–100
    double windSpeed,         // m/s
    WeatherCondition condition // enum with semantic abilities
) {}
```

### 5. The Architecture

**Abstractions (≥ 3, each with ≥ 3 concretions):**

1. **`game.weather.effect.WeatherEffect`** (interface) — A game-world effect that
   reads fields directly from {@link game.weather.WeatherApiResponse} and alters
   game state. Method: `apply(actor, map, location, weather) → String`.

   *Concrete classes:*
   | Class | Data Used | Effect |
   |-------|-----------|--------|
   | `game.weather.effect.HealthEffect` | Temperature | Applies BurnStatus (heat) or DizzyStatus (cold); heals in mild weather |
   | `game.weather.effect.SpawnRateEffect` | Humidity + Temperature | Spawns creatures via SpawnEffectManager or despawns hostile actors |
   | `game.weather.effect.TerrainEffect` | All fields | Dispatches weather data to all WeatherAffected ground tiles |

2. **`game.weather.alerts.WeatherAlert`** (interface) — A threshold-based alert
   that triggers an additional gameplay consequence when extreme weather is
   detected. Methods: `isTriggered(weather) → boolean`,
   `apply(actor, map, location, weather) → String`.

   *Concrete classes:*
   | Class | Threshold | Consequence |
   |-------|-----------|-------------|
   | `game.weather.alerts.ExtremeTemperatureAlert` | temp < −5°C or > 40°C | Stronger DizzyStatus or BurnStatus |
   | `game.weather.alerts.HighWindAlert` | wind > 20 m/s | Spreads Fire tiles to adjacent tiles |
   | `game.weather.alerts.HeavyRainAlert` | humidity ≥ 85% + rain condition | Extinguishes Fire tiles |

3. **`game.capabilities.WeatherAffected`** (interface) — A callback interface
   for ground tiles that react to weather. Method:
   `onWeatherChange(location, weather)`.

   *Concrete classes:*
   | Class | Ability Marker | Reaction |
   |-------|---------------|----------|
   | `game.grounds.Dirt` | `FLOODABLE` | High humidity → floods into Puddle |
   | `game.grounds.Puddle` | `EVAPORATABLE` | Low humidity → evaporates into Dirt |
   | `game.grounds.Fire` | `ON_FIRE` | Rain condition → reverts to original ground |

**Higher-level classes that depend only on the abstractions (DIP, ≥ 2):**

| Class | Type | Package | Role | Depends On |
|-------|------|---------|------|------------|
| `WeatherScanAction` | `Action` | `game.actions` | Fetches weather via API, applies all effects and alerts | `WeatherEffect`, `WeatherAlert` |
| `EnvironmentalTerminal` | `Ground` | `game.grounds` | Terminal tile offering the scan action; wired with effects+alerts at composition root | `WeatherEffect`, `WeatherAlert` |

**Supporting classes (not counted toward rule requirements):**

| Class | Package | Purpose |
|-------|---------|---------|
| `WeatherApiClient` | `game.weather` | Makes HTTP GET via `java.net.http.HttpClient`; parses JSON manually (no external deps) |
| `WeatherApiResponse` | `game.weather` | Java record holding raw temp, humidity, wind speed, condition |
| `WeatherCondition` | `game.weather` | Enum mapping API condition strings to semantic `Ability` values (e.g. `RAIN` carries `Ability.EXTINGUISH`) |

**Composition wiring** — All concrete implementations are wired into the
`EnvironmentalTerminal` at the composition root (`EclipseNebula.initialise()`):

```java
EnvironmentalTerminal terminal = new EnvironmentalTerminal(
    List.of(new HealthEffect(), new SpawnRateEffect(), new TerrainEffect()),
    List.of(new ExtremeTemperatureAlert(), new HighWindAlert(), new HeavyRainAlert())
);
```

No class in the weather system references a concrete effect or alert
implementation outside of this single composition root.

### 6. Security

The API key is **never committed to the repository**. It is read at runtime
from the `OPENWEATHER_API_KEY` environment variable.

#### Getting an API Key

1. Go to https://openweathermap.org/appid and click **Sign Up** or **Get API Key**.
2. Create a free account.
3. Once logged in, go to the **API Keys** tab in your account dashboard.
4. A default key is generated automatically — copy it.
   - Free tier allows 60 calls/minute, more than enough for this game.
   - The key activates within a few hours of sign-up.

#### Setting the Environment Variable

To configure it permanently:
- **Windows**: System Properties → Environment Variables → New User Variable
  `OPENWEATHER_API_KEY` = `your_key_here`
  → You must **fully restart IntelliJ** (exit the IDE and relaunch) after setting
  the variable — IntelliJ only reads Windows user environment variables at startup.
- **macOS / Linux**: Add `export OPENWEATHER_API_KEY="your_key_here"` to
  `~/.bashrc`, `~/.zshrc`, or `~/.profile`.

Then launch the game normally:
```bash
mvn compile exec:java
```

If no key is set or the API call fails, the system falls back to mild default
weather (20°C, 50% humidity) so the game remains playable regardless.
