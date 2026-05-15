package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The Meeple's compact strike module. Deals 5 damage with a 60% hit rate.
 * Primarily defensive — the Meeple relies on its state abilities rather
 * than raw combat output.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class MeepleFist extends IntrinsicWeapon {

    private static final int DAMAGE = 5;
    private static final int HIT_RATE = 60;

    /** Constructor. */
    public MeepleFist() {
        super(DAMAGE, "strikes", HIT_RATE, "strike module");
    }
}
