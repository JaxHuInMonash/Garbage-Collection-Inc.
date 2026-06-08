package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Abstract base for status effects that deal a fixed amount of damage each
 * turn for a limited duration. Subclasses only need to call {@code super}
 * with the appropriate parameters.
 * <p>
 * Implements {@link Cleansable} so any subclass (Poison, Burn, …) is
 * automatically removed in flight by a worker carrying a
 * {@link CleanseFieldStatus} (Healer T2) — see
 * {@link game.actors.ContractedWorker#addStatus}.
 *
 * @author Jax
 */
public abstract class TimedDamageStatus implements Cleansable {

    private final int damagePerTurn;
    private int remainingTurns;

    /**
     * Constructor.
     *
     * @param damagePerTurn the amount of damage dealt each turn
     * @param duration      the number of turns this effect lasts
     */
    protected TimedDamageStatus(int damagePerTurn, int duration) {
        this.damagePerTurn = damagePerTurn;
        this.remainingTurns = duration;
    }

    /**
     * Each tick, deal damage to the entity and decrement the remaining duration.
     *
     * @param currEntity the entity afflicted with this status
     * @param location   the location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        currEntity.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, damagePerTurn);
        remainingTurns--;
    }

    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    /**
     * Returns the damage dealt per turn.
     *
     * @return damage per turn
     */
    protected int getDamagePerTurn() {
        return damagePerTurn;
    }

    /**
     * Returns the number of turns remaining for this status.
     *
     * @return remaining turns
     */
    protected int getRemainingTurns() {
        return remainingTurns;
    }
}
