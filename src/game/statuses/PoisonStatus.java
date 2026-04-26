package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A poison status effect that deals damage to the afflicted entity each turn
 * for a specified duration.
 * <p>
 * Poison is applied when consuming toxic items (e.g., unsanitised Apple) or
 * drinking from unsterilised Puddles.
 *
 * @author Jax
 */
public class PoisonStatus implements Status {

    private final int damagePerTurn;
    private int remainingTurns;

    /**
     * Constructor.
     *
     * @param damagePerTurn the amount of damage dealt each turn
     * @param duration      the number of turns this poison lasts
     */
    public PoisonStatus(int damagePerTurn, int duration) {
        this.damagePerTurn = damagePerTurn;
        this.remainingTurns = duration;
    }

    /**
     * Each tick, deal poison damage to the entity and decrement the remaining duration.
     *
     * @param currEntity the entity afflicted with poison
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
}
