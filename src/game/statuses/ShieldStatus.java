package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A nanobot shield that absorbs damage before it reaches the bearer's HP.
 * <p>
 * Applied to Workers by the Meeple's GuardianState Nanobot Injection.
 * Each tick this status compares the bearer's current HP against their HP
 * at the last tick; if HP fell, the shield restores up to the loss amount
 * (consuming shield points). The remaining {@code shieldPoints} acts as the
 * "Shield Battery" counter checked by the Meeple for its Overloaded transition.
 */
public class ShieldStatus implements Status {

    private int shieldPoints;
    private int lastHp;
    private boolean initialised;

    /**
     * Constructor.
     *
     * @param shieldPoints total HP this shield can absorb
     */
    public ShieldStatus(int shieldPoints) {
        this.shieldPoints = shieldPoints;
        this.initialised = false;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        int currentHp = currEntity.getStatistic(ActorStatistics.HEALTH);
        if (!initialised) {
            lastHp = currentHp;
            initialised = true;
            return;
        }
        int damageTaken = lastHp - currentHp;
        if (damageTaken > 0 && shieldPoints > 0) {
            int restored = Math.min(damageTaken, shieldPoints);
            currEntity.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.INCREASE, restored);
            shieldPoints -= restored;
        }
        lastHp = currEntity.getStatistic(ActorStatistics.HEALTH);
    }

    @Override
    public boolean isStatusActive() {
        return shieldPoints > 0;
    }

    public int getShieldPoints() {
        return shieldPoints;
    }
}
