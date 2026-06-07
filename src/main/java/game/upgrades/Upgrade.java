package game.upgrades;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstraction #1 of REQ3 (Worker Progression System).
 * <p>
 * An {@code Upgrade} is a permanent enhancement that can be installed onto a
 * worker at an {@link UpgradeStation}. It owns:
 * <ul>
 *   <li>its <strong>identity</strong> — a path ({@link UpgradePath}) and a tier (1–3);</li>
 *   <li>its <strong>cost</strong> — both worker credits and company credits, so the
 *       feature is wired into the REQ1 quota economy;</li>
 *   <li>a <strong>one-shot effect</strong> ({@link #applyTo(Actor)}) run once at install time;</li>
 *   <li>an optional <strong>per-turn passive</strong> ({@link #tick(Actor, Location)}) driven each
 *       turn by the worker's {@link WorkerProgression}.</li>
 * </ul>
 * Concrete upgrades must produce <em>cross-component</em> effects (status,
 * terrain change, AoE, spawn, intrinsic-weapon swap, …), never a bare stat tweak.
 * <p>
 * Depends on the engine's {@link Actor} rather than the concrete worker class so
 * an upgrade never couples to {@code ContractedWorker}.
 *
 * @author Jax
 */
public interface Upgrade {

    /** Human-readable name shown in menus and logs. */
    String getName();

    /** The specialisation track this upgrade belongs to. */
    UpgradePath getPath();

    /** Tier within the path: 1, 2, or 3. A worker must own tier n before tier n+1. */
    int getTier();

    /** Cost in the worker's personal credits (A2 REQ1 wallet). */
    int getWorkerCreditCost();

    /** Cost in Company Credits (A3 REQ1 quota pool) — settled via {@link CompanyAccount}. */
    int getCompanyCreditCost();

    /**
     * One-shot effect applied the moment this upgrade is installed.
     * Examples: bump max HP, swap the intrinsic weapon, attach a persistent
     * combat status the worker's {@code hurt()} consults.
     *
     * @param worker the worker receiving the upgrade
     */
    void applyTo(Actor worker);

    /**
     * Per-turn passive, invoked by {@link WorkerProgression#tick(Actor, Location)}
     * every turn after installation. Default is a no-op for upgrades whose effect
     * is entirely one-shot.
     *
     * @param worker   the upgraded worker
     * @param location the worker's current location (for AoE / terrain effects)
     */
    default void tick(Actor worker, Location location) {
    }
}
