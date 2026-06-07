package game.statuses;
import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
/**
 * A status effect applied to a {@link game.actors.ContractedWorker} who reaches
 * infraction level 3 when caught by an {@link game.actors.EnforcerBot}.
 * <p>
 * For the configured number of turns, the Worker is locked out and cannot
 * perform any non-movement action. This is enforced in
 * {@link game.actors.ContractedWorker#playTurn} by checking for active instances
 * of this status and returning a DoNothingAction instead of showing the menu.
 *
 * @author Zhengjun Yang
 */
public class LockedOutStatus implements Status {
    private int turnsRemaining;
    /**
     * Constructor.
     *
     * @param turns the number of turns the lockout lasts
     */
    public LockedOutStatus(int turns) { this.turnsRemaining = turns; }
    /**
     * Decrements the remaining lockout turns by 1 each turn.
     *
     * @param currEntity the entity this status is attached to
     * @param location   the current location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        turnsRemaining--;
    }
    /**
     * Returns true while there are turns remaining on the lockout.
     *
     * @return true if the lockout is still active
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
    /**
     * Returns a string describing the lockout and how many turns remain.
     *
     * @return the status description
     */
    @Override
    public String toString() {
        return "LockedOut (" + turnsRemaining + " turns remaining)";
    }
}

