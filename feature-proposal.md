# Feature Proposal — Assignment 3

**Team:** CL_AppliedSession21TeamD
**Maintainer:** Jax
**Last updated:** 07/06/2026

## REQ3 — Custom Feature #1

**Owner:** Jax

### 1. The Pitch

> Workers start as Level 0 grunts. By spending both Worker Credits and Company Credits at a path-specific Lab Station, they specialise along one of three mutually exclusive tracks — Combat, Surveyor, or Healer — gaining permanent cross-component abilities at Levels 1, 2, and 3. Choosing a path is a strategic commitment: Company Credits spent here are not available for the quota deadline, forcing a high-stakes trade-off between personal power and contract survival.

### 2. The Mechanics

- A worker has a single UpgradePath (or NONE) and a tier from 0–3.
- A Lab Station (Combat / Surveyor / Healer) is a ground tile in the 99-Deprecated ship.
- The Lab Station offers only the next tier of its own path, and only to workers on that path or unspecialised.
- Each tier costs Worker Credits + Company Credits (50/30, 150/100, 300/250).
- Path locks at L1 — once you upgrade Combat L1, you cannot upgrade Surveyor or Healer.
- Every applied upgrade installs a passive tick effect, plus a one-shot on-apply effect. Effects are specified in the 'per-upgrade list' (see below).
- When the facility alarm is triggered, all Lab Stations refuse upgrades for 10 turns.

### 3. The Architecture

**Abstractions**
1. `game.upgrades.Upgrade` — a permanent enhancement that can be applied to a worker. Knows its costs (both currencies), its path, its tier, an applyTo(worker) method (one-shot install), and a tick(worker, location) (per-turn passive).
2. `game.upgrades.UpgradeStation` — abstract Ground subclass placed in safe zones. Knows which path it offers; surfaces only the upgrades the standing worker is currently eligible for.

**Concrete classes**

Implements `Upgrade`
1. CombatConditioning (NEW): On apply: +5 max HP and installs a passive — whenever the worker is hit, 40% chance to inflict BurnStatus(1,3) on the attacker. (status × combat)
2. SurveyorOptics (NEW): On apply: each turn, applies a Revealed status to any Hole/Vent within 5 tiles; revealed spawners broadcast "spawn imminent" 1 turn before firing. (status × ground × spawner)
3. HealerBioSync (NEW): On apply: each turn, all other workers within 2 tiles regenerate 2 HP; if none are adjacent, the healer drops a StasisDroplet item on its tile (free heal pickup). (AoE × inter-actor × item-spawn)  
Implements `UpgradeStation`
4. FighterStation (NEW): Ground tile that gates Combat-path upgrades; rejects workers already on Surveyor/Healer paths with a clear message
5. SurveyorStation (NEW): Same shape but only sells Surveyor upgrades
6. HealerStation (NEW): Same shape but only sells Healer upgrades

Per-upgrade list ↓  
Fighter path  
Implements `Upgrade`  
L1 CombatConditioning — see above (retaliation burn).  
L2 ReinforcedPlates — armor that takes the next 5 incoming damage instances, but each absorbed hit converts to Heat, and at 5 stacks the worker emits an AoE shockwave damaging adjacent hostiles for 3. (state machine on the worker + AoE)  
L3 PlasmaGauntlet — replaces the worker's intrinsic weapon. Every attack ignites the target's tile and the tile behind it for 3 turns; if the target dies, all hostiles within 2 tiles get BurnStatus. (terrain × AoE × kill-trigger)  

Surveyor path  
Implements `Upgrade`  
L1 SurveyorOptics — see above.  
L2 Magboots — move +1 per turn; walking on a Puddle does not poison; instead the puddle "rides along" with the worker for 3 tiles before dissipating (terrain physically moves). Walking on ToxicWaste reverts that tile to Floor after the worker steps off. (terrain modification, terrain follows actor)  
L3 PhaseStabilizer — once per 10 turns the worker may walk through a Wall; the wall converts to Floor permanently. WarperTreeMature and MagicCircle no longer teleport the worker randomly; WarperTreeMature stages a pending warp the worker can accept or decline on their next turn (no longer forced). MagicCircle presents one destination choice per other circle on the map, letting the worker pick exactly where to land. (unique interaction on existing ground types)  

Healer path  
Implements `Upgrade`  
L1 HealerBioSync — see above.  
L2 CleanseField — immune to Poison and Burn; standing adjacent to Fire / ToxicWaste reverts those tiles to their original Ground over 2 turns. (status immunity + active terrain purification)  
L3 ResurrectionProtocol — when this worker falls unconscious, they leave a StasisPod ground tile and are removed from play. Any other worker who steps on the pod consumes it and revives the original worker on that tile at 10 HP. The original worker keeps all upgrades but loses 30% of their worker-credit balance. (inter-actor revive, terrain spawn, persistent state across death)  

### 4. Design Decisions

- **No `Fighter` / `Surveyor` / `Healer` subclasses of `ContractedWorker`.** Specialisation is a *runtime, mutable* role — a worker starts un-specialised, locks a path at L1, then climbs tiers. Inheritance models *is-a* relationships fixed at construction; reclassing an existing instance is not a thing in Java. We therefore compose role via `WorkerProgression` (held by the worker) and `Upgrade` objects (which carry the behaviour). The three "roles" surface where they belong as real things in the world: as the three `UpgradeStation` concretes (`CombatStation`, `SurveyorStation`, `HealerStation`).
- **No `Specialisation` strategy class wrapping each path.** We considered an abstract `Specialisation` with three subclasses owning their catalogues + a path identity. It would be a *parallel structure with no behaviour of its own* — a "Lazy Class" smell — because every path-specific behaviour already lives inside its individual `Upgrade` concretes. We'll revisit only if a path needs cross-tier shared state (e.g. "any Combat worker permanently resists fire regardless of tier").
- **Stations discover the worker via `asCapability(Progressable.class)`, not `instanceof`.** This mirrors the engine's existing capability pattern (`getActorAs`, `Infectable`, etc.) and keeps `UpgradeStation` decoupled from the concrete `ContractedWorker` class — adding new "upgradable" actor types in the future requires zero changes to the station.
- **`CompanyAccount` is an interface owned by REQ3 but implemented by REQ1.** Dependency Inversion: REQ3 depends on the abstraction `canAfford / deduct`; REQ1's quota class becomes the concrete. This lets the two requirements progress independently and stops REQ3 from coupling to a teammate's internals.
- **`ApplyUpgradeAction` re-validates eligibility before charging anything.** Same "charge nothing unless all checks pass" discipline as the fixed `BuyAction` — no half-states, no lost credits on a failed install.

---

## REQ4 — Custom Feature #2

**Owner:** Zhengjun Yang

### 1. The Pitch
>  When a Worker commits a violation — such as trespassing into a restricted zone, attacking a teammate, or stealing equipment — the Infraction System records and escalates their infraction level (0–3). Once a Worker reaches Level 1, the Company remotely spawns an EnforcerBot onto the map to pursue them. EnforcerBots cycle through three behaviours: patrolling when idle, chasing when a violator is detected, and despawning when the target is gone. On capture, the bot deducts Company Credits and resets the infraction counter. Reaching Level 3 triggers an immediate fine and a temporary action lockout, forcing players to weigh risky shortcuts against financial and operational consequences.


### 2. The Mechanics
- Every Worker has an **infraction level** tracked by the `InfractionManager` singleton (levels 0–3).
- Certain in-game actions trigger an `InfractionTrigger`, incrementing the violating Worker's infraction level:
  - **ZoneTrespassTrigger**: stepping onto a `RestrictedGround` tile increments level by 1.
  - **FriendlyFireTrigger**: a Worker's attack hitting another Worker increments level by 1.
  - **SabotageTrigger**: a Worker uses an AlienCube, corrupting surrounding tiles and incrementing their infraction level by 1.
- When a Worker's infraction level reaches **Level 1 or above**, `InfractionManager` spawns an `EnforcerBot` onto the map near the violating Worker.
- The `EnforcerBot` cycles through three behaviours each turn:
  - **PatrolBehaviour**: when no violator is nearby, the bot wanders the map randomly.
  - **ChaseAndFineAction**: when the violator is detected, the bot pathfinds toward the target one step per turn.
  - **SurrenderDetectBehaviour**: checks each turn whether the target is unconscious or gone; if so, despawns the bot immediately.
- When the `EnforcerBot` reaches the Worker (adjacent tile), it:
  1. Deducts **Company Credits** proportional to infraction level (L1: 50, L2: 150, L3: 300).
  2. At **Level 3**, also applies a `LockedOutStatus` to the Worker for 5 turns (cannot perform actions other than moving).
  3. Resets the Worker's infraction level to 0 and removes itself from the map.


### 3. The Architecture

**Abstractions**
1. `game.penalty.InfractionTrigger` — Interface; any game event that can cause a violation implements `checkAndApply(Actor violator, GameMap map)`, which calls `InfractionManager` to increment the infraction level and conditionally spawn an `EnforcerBot`.
2. `game.penalty.EnforcerBehaviour` — Abstract class extending `Behaviour`; defines the contract for all EnforcerBot actions, with subclasses implementing `operate(Actor bot, Location location)` to pursue, patrol, or despawn.

**Concrete classes**

Implements `InfractionTrigger`
1. ZoneTrespassTrigger (NEW): Fires when a Worker steps onto a RestrictedGround tile; increments the Worker's infraction level by 1 via InfractionManager, and triggers EnforcerBot spawn if level ≥ 1. (ground × actor-state × spawning)
2. FriendlyFireTrigger (RETROFITTED): Attached to the existing AttackAction; checks whether the attack target is a fellow Worker and if so calls InfractionManager.increment(), then conditionally spawns an EnforcerBot. (action × actor-state × spawning)
3. SabotageTrigger (NEW): Fires when a Worker uses an AlienCube, corrupting the surrounding terrain into ToxicWaste. Increments the infraction level and triggers EnforcerBot spawn if level ≥ 1. (item × terrain × actor-state × spawning)

Implements `EnforcerBehaviour`
4. ChaseAndFineAction (NEW): Each turn, moves the EnforcerBot one step toward the target Worker via pathfinding. When adjacent, deducts Company Credits proportional to infraction level (L1: 50, L2: 150, L3: 300); at Level 3 also applies LockedOutStatus for 5 turns; then resets infraction level to 0 and removes the bot from the map. (actor-movement × currency × status × self-removal)
5. PatrolBehaviour (NEW): When no violating Worker is nearby, moves the EnforcerBot randomly around the map each turn, keeping it active and visible as a deterrent. (actor-movement × wander)
6. SurrenderDetectBehaviour (NEW): Each turn, checks whether the target Worker is unconscious or has left the map; if so, immediately removes the EnforcerBot from the map. (actor-state × self-removal)

---

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
