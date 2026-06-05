package game.weapons;


import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
/**
 * The intrinsic weapon of a {@link game.actors.ContractedWorker}.
 * <p>
 * Represents a bare-fisted punch with 2 damage and a 50% hit rate.
 * Used when a Worker attacks another Worker, triggering the
 * {@link game.penalty.FriendlyFireTrigger} infraction check in
 * {@link game.actions.AttackAction}.
 *
 * @author Zhengjun Yang
 */public class WorkerFist extends IntrinsicWeapon {
    private static final int DAMAGE = 2;
    private static final int HIT_RATE = 50;
    /**
     * Constructor.
     */
    public WorkerFist() {
        super(DAMAGE, "punches", HIT_RATE, "fist");
    }
}
