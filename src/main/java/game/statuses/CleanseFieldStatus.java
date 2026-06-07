package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Permanent passive marker installed on a worker by the
 * {@link game.upgrades.healer.CleanseField} upgrade (Healer T2).
 * <p>
 * Acts purely as a flag for two collaborators:
 * <ul>
 *   <li>{@link game.actors.ContractedWorker#addStatus(Status)} consults it to
 *       silently reject any incoming {@link TimedDamageStatus} (Poison, Burn) —
 *       the cleanse field's <em>immunity</em> aspect;</li>
 *   <li>{@link game.upgrades.healer.CleanseField#tick} does the
 *       terrain-purification scan independently.</li>
 * </ul>
 * Permanent — installed once by a permanent upgrade.
 *
 * @author Jax
 */
public class CleanseFieldStatus implements Status {

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        // No per-tick work — the immunity check happens in addStatus, the
        // terrain purification in the upgrade's tick.
    }

    @Override
    public boolean isStatusActive() {
        return true;
    }

    @Override
    public String toString() {
        return "Cleanse Field (immune to Poison & Burn; purifies adjacent hazards)";
    }
}
