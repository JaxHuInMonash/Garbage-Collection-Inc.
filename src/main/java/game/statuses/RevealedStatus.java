package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Marker status placed on a Ground (typically a spawner) by the Surveyor Optics
 * upgrade.
 * <p>
 * Has a turn-counter so the reveal naturally fades once the surveyor moves out
 * of range. The host Ground (e.g. {@link game.grounds.Hole}) is responsible for
 * calling {@code tickStatuses(location)} from its own {@code tick()} so this
 * counter actually ages — the engine doesn't tick ground statuses automatically.
 * <p>
 * Cross-component shape:
 * <ul>
 *   <li><strong>Surveyor Optics</strong> applies/refreshes it on nearby spawners each turn.</li>
 *   <li><strong>Hole/Vent</strong> consults {@code hasStatus(RevealedStatus.class)}
 *       to decide whether to broadcast warnings to the EventLog.</li>
 * </ul>
 *
 * @author Jax
 */
public class RevealedStatus implements Status {

    private int remainingTurns;

    /**
     * @param duration how many turns the reveal lasts before fading
     */
    public RevealedStatus(int duration) {
        this.remainingTurns = duration;
    }

    /**
     * Bumps the remaining duration back up — used when the surveyor remains in
     * range turn-after-turn.
     */
    public void refresh(int duration) {
        this.remainingTurns = Math.max(this.remainingTurns, duration);
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        remainingTurns--;
    }

    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    @Override
    public String toString() {
        return "Revealed (" + remainingTurns + " turns)";
    }
}
