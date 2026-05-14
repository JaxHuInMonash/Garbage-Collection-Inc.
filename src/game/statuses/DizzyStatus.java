package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A disorientation status that causes an actor to skip their turn.
 * <p>
 * Applied by the Meeple's EngineerState Repelling Pulse to adjacent
 * non-Meeple actors. Each affected actor's {@code playTurn()} checks for
 * this status and returns {@code DoNothingAction} if present, then
 * DizzyStatus ticks down by one.
 */
public class DizzyStatus implements Status {

    private int remainingTurns;

    /**
     * Constructor.
     *
     * @param turns how many turns the actor will be disoriented
     */
    public DizzyStatus(int turns) {
        this.remainingTurns = turns;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        remainingTurns--;
    }

    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }
}
