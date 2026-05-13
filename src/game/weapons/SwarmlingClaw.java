package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The Swarmling's serrated claw. Deals 8 damage with a 75% hit rate.
 * BerserkState's ChainStrikeAction uses {@code DAMAGE / 2 = 4} for
 * the shockwave portion.
 *
 * @author Ethan
 * @author Tommy
 */
public class SwarmlingClaw extends IntrinsicWeapon {

    /** Base damage for the claw attack. Chain Strike deals DAMAGE/2. */
    private static final int DAMAGE = 8;
    private static final int HIT_RATE = 75;

    /** Constructor. */
    public SwarmlingClaw() {
        super(DAMAGE, "slashes", HIT_RATE, "serrated claw");
    }
}
