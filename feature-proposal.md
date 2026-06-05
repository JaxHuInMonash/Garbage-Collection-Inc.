# Feature Proposal — Assignment 3

**Team:** CL_AppliedSession21TeamD
**Maintainer:** Jax
**Last updated:** 03/06/2026

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

A few deliberate calls worth recording for the rationale writeup and TA discussion:

- **No `Fighter` / `Surveyor` / `Healer` subclasses of `ContractedWorker`.** Specialisation is a *runtime, mutable* role — a worker starts un-specialised, locks a path at L1, then climbs tiers. Inheritance models *is-a* relationships fixed at construction; reclassing an existing instance is not a thing in Java. We therefore compose role via `WorkerProgression` (held by the worker) and `Upgrade` objects (which carry the behaviour). The three "roles" surface where they belong as real things in the world: as the three `UpgradeStation` concretes (`CombatStation`, `SurveyorStation`, `HealerStation`).
- **No `Specialisation` strategy class wrapping each path.** We considered an abstract `Specialisation` with three subclasses owning their catalogues + a path identity. It would be a *parallel structure with no behaviour of its own* — a "Lazy Class" smell — because every path-specific behaviour already lives inside its individual `Upgrade` concretes. We'll revisit only if a path needs cross-tier shared state (e.g. "any Combat worker permanently resists fire regardless of tier").
- **Stations discover the worker via `asCapability(Progressable.class)`, not `instanceof`.** This mirrors the engine's existing capability pattern (`getActorAs`, `Infectable`, etc.) and keeps `UpgradeStation` decoupled from the concrete `ContractedWorker` class — adding new "upgradable" actor types in the future requires zero changes to the station.
- **`CompanyAccount` is an interface owned by REQ3 but implemented by REQ1.** Dependency Inversion: REQ3 depends on the abstraction `canAfford / deduct`; REQ1's quota class becomes the concrete. This lets the two requirements progress independently and stops REQ3 from coupling to a teammate's internals.
- **`ApplyUpgradeAction` re-validates eligibility before charging anything.** Same "charge nothing unless all checks pass" discipline as the fixed `BuyAction` — no half-states, no lost credits on a failed install.

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


**Higher-level classes that depend only on the abstractions (≥ 2, demonstrating DIP)**