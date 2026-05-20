package game.statuses;

/**
 * A burn status effect that deals fire damage to the afflicted entity each turn
 * for a specified duration.
 * <p>
 * Burn is applied when an actor stands on a Fire ground tile.
 *
 * @author Jax
 */
public class BurnStatus extends TimedDamageStatus {

    /**
     * Constructor.
     *
     * @param damagePerTurn the amount of damage dealt each turn
     * @param duration      the number of turns this burn lasts
     */
    public BurnStatus(int damagePerTurn, int duration) {
        super(damagePerTurn, duration);
    }

    @Override
    public String toString() {
        return "Burn (" + getDamagePerTurn() + " dmg/turn, " + getRemainingTurns() + " turns left)";
    }
}
