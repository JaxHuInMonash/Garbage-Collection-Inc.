package game.capabilities;

import game.upgrades.WorkerProgression;

/**
 * Capability marker for actors that can receive upgrades from an
 * {@link game.upgrades.UpgradeStation}.
 * <p>
 * Implemented by {@code ContractedWorker}. The station discovers it
 * polymorphically through the engine's
 * {@link edu.monash.fit2099.engine.GameEntity#asCapability(Class)} — so no
 * {@code instanceof} or downcast to the concrete worker class is ever needed.
 *
 * @author Jax
 */
public interface Progressable {

    /**
     * @return the progression state for this actor (never null)
     */
    WorkerProgression getProgression();
}
