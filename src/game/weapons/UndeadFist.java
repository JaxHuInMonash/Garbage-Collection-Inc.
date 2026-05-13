package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The bare fist of an Undead creature. Deals 1 damage with a 10% hit rate.
 *
 * @author Jax
 * @author Tommy
 */
public class UndeadFist extends IntrinsicWeapon {
    private static final int DAMAGE = 1;
    private static final int HIT_RATE = 10;

    /**
     * Constructor.
     */
    public UndeadFist() {
        super(DAMAGE, "punches", HIT_RATE, "bare fist");
    }
}
