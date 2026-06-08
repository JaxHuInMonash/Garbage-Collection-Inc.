package game.statuses;

import edu.monash.fit2099.engine.capabilities.Status;

/**
 * Marker interface for any {@link Status} that should be silently dropped by
 * a worker carrying a {@link CleanseFieldStatus} (Healer T2).
 * <p>
 * Introducing this capability means {@link game.actors.ContractedWorker#addStatus(Status)}
 * can decide whether to admit an incoming status without coupling to the
 * concrete {@link TimedDamageStatus} hierarchy — any future status type (a
 * Curse, a Bleed, a Radiation tick) can opt into cleansing by implementing
 * this interface, no edits to the worker required (OCP).
 * <p>
 * <em>Note:</em> the engine's {@code Status} interface doesn't extend
 * {@code GameEntity}, so it has no {@code hasCapability} / {@code asCapability}
 * lookup. A marker interface is the canonical Java substitute when capability
 * dispatch isn't available; the resulting {@code instanceof Cleansable} check
 * tests a role, not a class.
 *
 * @author Jax
 */
public interface Cleansable extends Status {
}
