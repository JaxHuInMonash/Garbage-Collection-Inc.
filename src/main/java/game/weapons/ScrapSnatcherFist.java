package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The rabid claw-swipe of an infected Scrap Snatcher.
 * Deals 1 damage with a 10% hit rate — frenzied but wildly inaccurate.
 *
 * @author Ethan
 */
public class ScrapSnatcherFist extends IntrinsicWeapon {

    private static final int DAMAGE = 1;
    private static final int HIT_RATE = 10;

    /** Constructor. */
    public ScrapSnatcherFist() {
        super(DAMAGE, "claws at", HIT_RATE, "bare claw");
    }
}
