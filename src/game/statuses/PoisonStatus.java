package game.statuses;

/**
 * A poison status effect that deals damage to the afflicted entity each turn
 * for a specified duration.
 * <p>
 * Poison is applied when consuming toxic items (e.g., unsanitised Apple) or
 * drinking from unsterilised Puddles.
 *
 * @author Jax
 */
public class PoisonStatus extends TimedDamageStatus {

    /**
     * Constructor.
     *
     * @param damagePerTurn the amount of damage dealt each turn
     * @param duration      the number of turns this poison lasts
     */
    public PoisonStatus(int damagePerTurn, int duration) {
        super(damagePerTurn, duration);
    }
}
