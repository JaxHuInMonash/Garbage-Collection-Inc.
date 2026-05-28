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
(temperature, humidity, wind speed) then drives three distinct game-world
effects: environmental HP damage/healing across all actors, creature spawns
based on humidity and temperature, and terrain transformations (flooding and
evaporation of Puddle tiles).

### 2. The Mechanics

- An **Environmental Terminal** (`¶`) is placed on the 99-Deprecated map as a
  wall-mounted terminal. It cannot be entered but offers a **Weather Scan**
  action when the worker is standing on an adjacent tile.
- When activated, the **Weather Scan Action**:
  1. Reads the worker's current map position `(x, y)` and uses them as
     `(lon, lat)` for the OpenWeatherMap API query.
  2. Sends an HTTP GET to `https://api.openweathermap.org/data/2.5/weather`
     with the dynamically-determined lat/lon, metric units, and the API key
     from the `OPENWEATHER_API_KEY` environment variable.
  3. Parses the JSON response manually (no external JSON library) into
     normalised data points (temperature → 0–1, humidity → 0–1, wind speed → 0–1).
  4. Applies three independent effects to the current game map, each
     integrating with existing game mechanics:

     **a. Health Effect** — Temperature-driven, uses existing status system:
     - ≤ 5°C: applies {@link DizzyStatus} (skip 1 turn) to all unprotected actors
     - 15–25°C: heals all actors by 1 HP
     - ≥ 30°C: applies {@link BurnStatus} (1 dmg/turn, 3 turns) to all unprotected actors
     - Workers with a SterilisationBox ({@link Ability#HAS_STERILISATION}) are
       protected from temperature extremes (reuses existing game mechanic)

     **b. Spawn Rate Effect** — Humidity + Temperature, tracked by existing
        {@link SpawnEffectManager}:
     - Hot (≥ 0.5 norm) + Humid (≥ 0.6 norm): spawns a creature (Slime, Undead,
       or Parasite depending on exact conditions) adjacent to the worker;
       registered with the existing SpawnEffectManager for alarm system tracking
     - Cold (≤ 0.25 norm) + Dry (≤ 0.3 norm): removes a random creature with
       {@link Ability#HOSTILE_TO_WORKERS} from the map

     **c. Terrain Effect** — Humidity-driven, respects existing ground abilities:
     - High humidity (≥ 0.7): converts up to 3 Dirt tiles adjacent to Puddles
       into new Puddles (flooding)
     - Low humidity (≤ 0.3): converts up to 3 Puddle tiles into Dirt
       (evaporation)
     - Tiles with {@link Ability#BLOCKS_ENTRY} (walls, doors) are preserved

- If the API call fails (no network, missing key, etc.), a safe fallback
  response (20°C, 50% humidity, 2 m/s wind, Clear) is used so the game remains
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

### 5. The Architecture

**Abstractions (≥ 2, each with ≥ 3 concretions):**

1. **`game.weather.WeatherDataPoint`** (interface) — A single normalised data
   field extracted from the API response. Method: `readFrom(response)`,
   `getNormalisedValue() → 0.0–1.0`, `getLabel()`.

   *Concrete classes:*
   | Class | Source | Normalisation |
   |-------|--------|---------------|
   | `TemperatureDataPoint` | `main.temp` | -5°C→0.0, 45°C→1.0 |
   | `HumidityDataPoint` | `main.humidity` | 0%→0.0, 100%→1.0 |
   | `WindSpeedDataPoint` | `wind.speed` | 0 m/s→0.0, 50 m/s→1.0 |

2. **`game.weather.WeatherEffect`** (interface) — A game-world effect that uses
   one or more `WeatherDataPoint` values to alter game state.

   *Concrete classes:*
   | Class | Data Used | Effect |
   |-------|-----------|--------|
   | `HealthEffect` | Temperature | Cold/heat damage or healing to all actors |
   | `SpawnRateEffect` | Humidity + Temperature | Spawn or despawn creatures |
   | `TerrainEffect` | Humidity | Convert Puddle↔Dirt tiles |

**Higher-level classes that depend only on the abstractions (DIP, ≥ 2):**

| Class | Type | Role | Dependencies |
|-------|------|------|-------------|
| `WeatherScanAction` | `Action` | Player-initiated weather scan at the terminal | `WeatherDataPoint`, `WeatherEffect` |
| `EnvironmentalTerminal` | `Ground` | Terminal tile that offers the scan action; wired with effects at composition root | `WeatherDataPoint`, `WeatherEffect` |

**Supporting classes (not counted toward rule requirements):**

| Class | Purpose |
|-------|---------|
| `WeatherApiClient` | Makes HTTP GET via `java.net.http.HttpClient`; parses JSON manually (no external dependencies) |
| `WeatherApiResponse` | Immutable data record holding raw temp, humidity, wind speed, condition |

### 6. Security

The API key is **never committed to the repository**. It is read at runtime from
the `OPENWEATHER_API_KEY` environment variable. To run this feature:

1. Sign up for a free OpenWeatherMap API key at
   https://openweathermap.org/appid.
2. Set the environment variable:
   - **Windows (CMD):** `set OPENWEATHER_API_KEY=your_key_here`
   - **Windows (PowerShell):** `$env:OPENWEATHER_API_KEY="your_key_here"`
   - **macOS/Linux:** `export OPENWEATHER_API_KEY=your_key_here`
3. Run the game normally. The terminal will use the key from the environment.
4. If no key is set or the API call fails, the system falls back to mild default
   weather (20°C, 50% humidity) so the game remains playable.
