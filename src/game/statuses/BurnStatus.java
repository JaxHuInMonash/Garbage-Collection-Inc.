package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A burn status effect that deals fire damage to the afflicted entity each turn
 * for a specified duration.
 * <p>
 * Burn is applied when an actor stands on a Fire ground tile.
 *
 * @author Jax
 */
public class BurnStatus implements Status {

    private final int damagePerTurn;
    private int remainingTurns;

    /**
     * Constructor.
     *
     * @param damagePerTurn the amount of damage dealt each turn
     * @param duration      the number of turns this burn lasts
     */
    public BurnStatus(int damagePerTurn, int duration) {
        this.damagePerTurn = damagePerTurn;
        this.remainingTurns = duration;
    }

    /**
     * Each tick, deal burn damage to the entity and decrement the remaining duration.
     *
     * @param currEntity the entity afflicted with burn
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
