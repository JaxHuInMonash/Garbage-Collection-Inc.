package game.upgrades;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-worker progression state: which path the worker locked into, their
 * current tier, and the list of installed {@link Upgrade}s whose passives must
 * tick each turn.
 * <p>
 * A {@code ContractedWorker} owns one of these and exposes it via the
 * {@link game.capabilities.Progressable} capability. Keeping the state in its
 * own class (rather than scattering fields across the worker) keeps the worker
 * lean and makes the progression logic independently testable.
 *
 * @author Jax
 */
public class WorkerProgression {

    private UpgradePath path = UpgradePath.NONE;
    private int tier = 0;
    private final List<Upgrade> installed = new ArrayList<>();

    /** @return the path this worker has committed to (NONE if un-specialised). */
    public UpgradePath getPath() {
        return path;
    }

    /** @return the highest tier installed so far (0 if none). */
    public int getTier() {
        return tier;
    }

    /**
     * Validates whether the given upgrade may be installed right now:
     * <ul>
     *   <li><strong>Path lock:</strong> the worker is un-specialised, or the upgrade
     *       belongs to the worker's chosen path.</li>
     *   <li><strong>Tier sequence:</strong> the upgrade is exactly the next tier up
     *       (no skipping; tier n+1 requires tier n).</li>
     * </ul>
     *
     * @param upgrade the candidate upgrade
     * @return true if installable
     */
    public boolean canApply(Upgrade upgrade) {
        boolean pathOk = (path == UpgradePath.NONE) || (path == upgrade.getPath());
        boolean tierOk = upgrade.getTier() == tier + 1;
        return pathOk && tierOk;
    }

    /**
     * Commits an upgrade: locks the path, advances the tier, records the upgrade,
     * and fires its one-shot {@link Upgrade#applyTo(Actor)} effect. Callers
     * (i.e. {@link game.actions.ApplyUpgradeAction}) must have already settled
     * the cost and confirmed {@link #canApply(Upgrade)}.
     *
     * @param upgrade the upgrade to install
     * @param worker  the worker receiving it
     */
    public void install(Upgrade upgrade, Actor worker) {
        this.path = upgrade.getPath();
        this.tier = upgrade.getTier();
        this.installed.add(upgrade);
        upgrade.applyTo(worker);
    }

    /**
     * Drives every installed upgrade's per-turn passive. Called once per turn
     * from the worker's {@code playTurn}.
     *
     * @param worker   the upgraded worker
     * @param location the worker's current location
     */
    public void tick(Actor worker, Location location) {
        for (Upgrade upgrade : installed) {
            upgrade.tick(worker, location);
        }
    }

    /** @return an unmodifiable view of installed upgrades (for display / tests). */
    public List<Upgrade> getInstalled() {
        return List.copyOf(installed);
    }
}
