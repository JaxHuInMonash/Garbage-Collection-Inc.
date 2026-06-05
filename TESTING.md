# Testing — FIT2099 Assignment 3 - REQ3

This document explains how to run the unit test suite, what's covered, and the conventions tests follow.

## How to run

From the project root:

```bash
mvn test
```

That triggers `maven-surefire-plugin` and runs everything under `src/test/java/**/*Test.java`. A summary appears in the terminal and full reports land in `target/surefire-reports/`.

To run just one test class:

```bash
mvn test -Dtest=WorkerProgressionTest
```

To run a single method:

```bash
mvn test -Dtest=ApplyUpgradeActionTest#happyPath
```

## What's tested (REQ3 — Worker Progression / Upgrade System)

| Test class | What it covers | # tests |
|---|---|---|
| `game.upgrades.InMemoryCompanyAccountTest` | Company-credit ledger: `canAfford` boundaries, deduct, deposit, the insufficient-funds throw | 7 |
| `game.upgrades.WorkerProgressionTest` | Path-lock + tier-sequence state machine; `canApply` happy/edge/invalid paths; `install` commits state and calls `applyTo`; `tick` drives all installed upgrades | 8 |
| `game.upgrades.UpgradeStationTest` | The Rule-1 base class: eligibility filter via the `Progressable` capability lookup, non-enterable, path-locked-worker sees no actions, fresh worker sees only tier 1 | 5 |
| `game.actions.ApplyUpgradeActionTest` | The four-guard buy pipeline: happy path, path/tier rejection, insufficient worker credits, insufficient company credits, exact-boundary success | 5 |
| `game.statuses.PendingWarpStatusTest` | Surveyor T3 staged warp: active duration, exposes destination + source label, ticks down to inactive, zero-duration edge | 4 |
| `game.statuses.PhaseStabilizerStatusTest` | Surveyor T3 charge: ready by default, consume disarms + starts cool-down, re-arms after exactly `RECHARGE_TURNS` ticks, permanent active | 5 |
| `game.statuses.ReinforcedPlatesStatusTest` | Combat T2: 50% absorption (with integer rounding edge), counter increments, AoE shockwave fires on threshold via mocked Location/Exit graph, counter resets | 6 |
| `game.statuses.RetaliationStatusTest` | Combat T1: 0% never triggers, 100% always triggers (boundary chances chosen so RNG doesn't make tests flaky), permanent active | 3 |
| `game.upgrades.healer.HealerBioSyncTest` | Healer T1 per-turn AoE heal: ally healed, self skipped, non-worker ignored, empty tile skipped | 4 |

**Total: 47 tests across 9 test classes.**

## Conventions

- **JUnit 5 + Mockito-core 3.2.4** — both declared in `pom.xml`, scope `test`.
- **No `@ExtendWith(MockitoExtension.class)`** — `mockito-junit-jupiter` isn't on the dependency list, so tests use `Mockito.mock(Class)` directly. Same end behaviour, no surprise extension setup.
- **Determinism over coverage of RNG paths** — anywhere a class uses `java.util.Random` internally, we hit only the boundary chance values (0% and 100%) so the outcome is fixed by construction. We don't reseed the RNG; we don't rely on time, network, or filesystem state.
- **Fixtures are local to each test method or `@BeforeEach`** — no shared mutable state between tests; running tests in any order produces the same result.
- **Naming** — `Class` + `Test` suffix; method names are sentences (`happyPath`, `rejectsInsufficientWorkerCredits`, `edge_zeroDurationStartsInactive`). `@DisplayName` is used on the larger test classes for friendlier surefire output.
- **Mocking strategy** — engine classes (`Actor`, `Location`, `GameMap`, `Exit`) are mocked because constructing real ones drags in too much engine state. REQ3 classes that are pure (state machines, value holders) are tested without mocks.

## Coverage of the rubric requirements

- ✅ **Three or more test values per logical unit** — every test class above has ≥ 3 tests; almost every class hits the typical / edge / invalid trio explicitly.
- ✅ **Deterministic** — boundary chance values for the RNG-using classes; no clock/network/filesystem dependencies.
- ✅ **Isolated** — Mockito stubs the engine; no real maps, real actors, or real ground.
- ✅ **Clear structure & naming** — one test class per production class; method names describe the case in English.
- ✅ **Documented execution** — this file plus `mvn test` is the entry point.
- ✅ **Sensible assertions** — `assertTrue/False`, `assertEquals`, `assertThrows`, plus Mockito `verify(...)` and `never()` for behavioural assertions. No "test passes if no exception" tests.