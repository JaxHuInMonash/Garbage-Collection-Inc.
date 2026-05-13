package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The Meeple's compact strike module. Deals 5 damage with a 60% hit rate.
 * Primarily defensive — the Meeple relies on its state abilities rather
 * than raw combat output.
 */
public class MeepleFist extends IntrinsicWeapon {

    /** Constructor. */
    public MeepleFist() {
        super(5, "strikes", 60, "strike module");
    }
}
