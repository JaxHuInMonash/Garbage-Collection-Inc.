package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Permanent passive marker installed on a worker by the
 * {@link game.upgrades.healer.ResurrectionProtocol} upgrade (Healer T3).
 * <p>
 * Acts as a flag consulted by {@link game.actors.ContractedWorker#unconscious}
 * — when the worker falls, the override drops a
 * {@link game.grounds.StasisPod} at their tile instead of removing them
 * cleanly, capturing the worker object so another worker can revive them
 * later.
 *
 * @author Jax
 */
public class ResurrectionProtocolStatus implements Status {

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        // No per-tick work — death-intercept happens in unconscious() override.
    }

    @Override
    public boolean isStatusActive() {
        return true;
    }

    @Override
    public String toString() {
        return "Resurrection Protocol (death drops a Stasis Pod)";
    }
}
