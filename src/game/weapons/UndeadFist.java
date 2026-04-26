package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The bare fist of an Undead creature. Deals 1 damage with a 10% hit rate.
 *
 * @author Jax
 */
public class UndeadFist extends IntrinsicWeapon {

    /**
     * Constructor.
     */
    public UndeadFist() {
        super(1, "punches", 10, "bare fist");
    }
}
