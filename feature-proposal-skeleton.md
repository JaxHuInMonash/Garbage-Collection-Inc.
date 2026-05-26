# Feature Proposal — Assignment 3

**Team:** CL_AppliedSession21TeamD
**Maintainer:** Jax
**Last updated:** 26/05/2026

## REQ3 — Custom Feature #1

**Owner:** Jax

### 1. The Pitch

> Workers start as Level 0 grunts. By spending both Worker Credits and Company Credits at a path-specific Lab Station, 
> they specialise along one of three mutually exclusive tracks — Combat, Surveyor, or Healer — gaining permanent 
> cross-component abilities at Levels 1, 2, and 3. Choosing a path is a strategic commitment: Company Credits spent 
> here are not available for the quota deadline, forcing a high-stakes trade-off between personal power and contract 
> survival.

### 2. The Mechanics

- A worker has a single UpgradePath (or NONE) and a tier from 0–3.
- A Lab Station (Combat / Surveyor / Healer) is a ground tile in the 99-Deprecated ship.
- The Lab Station offers only the next tier of its own path, and only to workers on that path or unspecialised.
- Each tier costs Worker Credits + Company Credits (50/30, 150/100, 300/250).
- Path locks at L1 — once you upgrade Combat L1, you cannot upgrade Surveyor or Healer.
- Every applied upgrade installs a passive tick effect, plus a one-shot on-apply effect. Effects are specified in the per-upgrade list (see code / README).
- When the facility alarm is triggered, all Lab Stations refuse upgrades for 10 turns.

### 3. The Architecture
_List the two new abstractions and the six concrete classes that satisfy them. Mark each concrete as **NEW** or **RETROFIT** (existing class modified to implement the abstraction)._

**Abstractions**
1. `game.upgrades.Upgrade` — a permanent enhancement that can be applied to a worker. Knows its costs (both currencies), its path, its tier, an applyTo(worker) method (one-shot install), and a tick(worker, location) (per-turn passive).
2. `game.upgrades.UpgradeStation` — abstract Ground subclass placed in safe zones. Knows which path it offers; surfaces only the upgrades the standing worker is currently eligible for.

**Concrete classes**

| # | Class      | Implements         | Status                       | One-line complex effect                                                                                                                           |
|---|------------|--------------------|------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | `CombatConditioning (L1 combat)` | `Upgrade`          | **NEW**                      | On apply: +5 max HP and installs a passive — whenever the worker is hit, 40% chance to inflict BurnStatus(1,3) on the attacker. (status × combat) |
| 2 | `SurveyorOptics (L1 surveyor)`         | `Upgrade`          | **NEW**                      | On apply: each turn, applies a Revealed status to any Hole/Vent within 5 tiles; revealed spawners broadcast "spawn imminent" 1 turn before firing. (status × ground × spawner)                                                                                                                                                  |
| 3 | `<package>.<ConcreteA3>` | `Upgrade`          | **RETROFIT** (`A1/A2 class`) | _…_                                                                                                                                               |
| 4 | `<package>.<ConcreteB1>` | `AbstractionName2` | **NEW**                      | _…_                                                                                                                                               |
| 5 | `<package>.<ConcreteB2>` | `AbstractionName2` | **NEW / RETROFIT**           | _…_                                                                                                                                               |
| 6 | `<package>.<ConcreteB3>` | `AbstractionName2` | **NEW / RETROFIT**           | _…_                                                                                                                                               |

> Self-check before submission:
> - [ ] ≥ 2 abstractions
> - [ ] ≥ 6 concrete classes total (≥ 3 per abstraction)
> - [ ] ≥ 3 are **NEW**, ≤ 3 are **RETROFIT**
> - [ ] Every effect is *cross-component* (Item→Action→Ground / Status / AoE / spawn / terrain change), not "heal X" or "print Y"
> - [ ] Idea is not a clone of an A1/A2/A3-REQ1/REQ2 mechanic (state machine, alarm/observer, teleport, infection, spawn-with-effect, door tier, buy/sell, quota deposit, plasma cut)

---

## REQ4 — Custom Feature #2

**Owner:** Zhengjun

### 1. The Pitch
> _TODO_

### 2. The Mechanics
- _TODO_

### 3. The Architecture

**Abstractions**
1. `<package>.<AbstractionName1>` — _one-line responsibility_
2. `<package>.<AbstractionName2>` — _one-line responsibility_

**Concrete classes**

| # | Class | Implements | Status | One-line complex effect |
|---|-------|------------|--------|--------------------------|
| 1 |  |  | **NEW / RETROFIT** |  |
| 2 |  |  | **NEW / RETROFIT** |  |
| 3 |  |  | **NEW / RETROFIT** |  |
| 4 |  |  | **NEW / RETROFIT** |  |
| 5 |  |  | **NEW / RETROFIT** |  |
| 6 |  |  | **NEW / RETROFIT** |  |

> Same self-check as REQ3.
> **Also:** REQ4 must be a *distinct* idea from REQ3. Don't just instance-swap nouns ("light system" then "sound system" with the same abstractions).

---

## REQ5 — External API Integration (HD)

**Owner:** Thomas
**API used:** _e.g. OpenWeatherMap, CoinGecko, FarmSense Moon Phase, …_

### 1. The Pitch
> _TODO_

### 2. The Mechanics
- _TODO — describe how API data alters game state, not just text output._

### 3. The Architecture

**Abstractions (≥ 2, each with ≥ 3 concretes)**
1. `<package>.<APIAbstraction1>` — _one-line responsibility_
2. `<package>.<APIAbstraction2>` — _one-line responsibility_

**Concrete classes**

| # | Class | Implements | One-line cross-component effect |
|---|-------|------------|----------------------------------|
| 1 |  |  |  |
| 2 |  |  |  |
| 3 |  |  |  |
| 4 |  |  |  |
| 5 |  |  |  |
| 6 |  |  |  |

**Higher-level classes that depend only on the abstractions (≥ 2, demonstrating DIP)**

| # | Class | Kind (Action / Behaviour / Actor / Ground / Map / Other) | What it does |
|---|-------|-----------------------------------------------------------|--------------|
| 1 |  |  |  |
| 2 |  |  |  |

### 4. The Request
_Example URL, with placeholders that are filled from current game state:_

```
GET https://api.example.com/v1/<endpoint>?lat={player.x}&lon={player.y}&hp={player.hp}&key=${ENV_API_KEY}
```

_Game-state variables driving the query:_
- `player.x`, `player.y` — _what they represent in the call_
- `player.hp` — _…_
- `<other>` — _…_

### 5. The Schema
_Example of the JSON shape the game parses:_

```json
{
  "field_a": 12.3,
  "field_b": "string-label",
  "nested": {
    "field_c": 7
  }
}
```

_Mapping of each JSON field → game effect:_
- `field_a` → _e.g. modifies fire damage multiplier this turn_
- `field_b` → _e.g. selects a spawn weighting profile_
- `nested.field_c` → _…_

### 6. Security & Setup
- API key is **never** committed. It is read from:
  - **Environment variable:** `API_KEY` _(or `<NAME>`)_
  - **Local file:** `.env` at repo root (already added to `.gitignore`)
- How to obtain the key:
  1. _Sign up at https://…_
  2. _Copy the key from your account dashboard_
  3. _Run `export API_KEY=…` (Linux/macOS) or `setx API_KEY …` (Windows PowerShell), OR_
  4. _Create `.env` with `API_KEY=…` in the repo root_

> Self-check before submission:
> - [ ] All REQ3/REQ4 rules satisfied here too (Rule 0)
> - [ ] API call uses dynamic query params from game state
> - [ ] API data creates/modifies/destroys game objects (not text-only)
> - [ ] At least 2 higher-level classes depend only on the abstractions (DIP)
> - [ ] `.env` is in `.gitignore` and no key has ever been committed
> - [ ] README has setup instructions for graders
