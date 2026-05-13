package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The laser weapon of a Security Drone. Deals 3 damage with a 50% hit rate.
 *
 * @author Jax
 * @author Tommy
 */
public class DroneLaser extends IntrinsicWeapon {
    private static final int DAMAGE = 3;
    private static final int HIT_RATE = 50;

    /**
     * Constructor.
     */
    public DroneLaser() {
        super(DAMAGE, "zaps", HIT_RATE, "laser");
    }
}
