package game.upgrades;

/**
 * The three mutually exclusive specialisation tracks a worker may commit to,
 * plus {@link #NONE} for an un-specialised (Level 0) worker.
 * <p>
 * A worker locks into one path the moment they install their first
 * {@link Upgrade}; from then on, only upgrades of that same path may be applied
 * (see {@link WorkerProgression#canApply(Upgrade)}).
 *
 * @author Jax
 */
public enum UpgradePath {
    /** No specialisation yet — the worker may still choose any path. */
    NONE,
    /** Offensive enhancements (retaliation, armour shockwave, plasma weapon). */
    FIGHTER,
    /** Mobility and reconnaissance enhancements. */
    SURVEYOR,
    /** Support and restoration enhancements. */
    HEALER
}
