package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The laser weapon of a Security Drone. Deals 3 damage with a 50% hit rate.
 *
 * @author Jax
 */
public class DroneLaser extends IntrinsicWeapon {

    /**
     * Constructor.
     */
    public DroneLaser() {
        super(3, "zaps", 50, "laser");
    }
}
