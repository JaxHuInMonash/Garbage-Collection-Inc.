package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The Swarmling's serrated claw. Deals 8 damage with a 75% hit rate.
 * BerserkState's ChainStrikeAction uses {@code DAMAGE / 2 = 4} for
 * the shockwave portion.
 */
public class SwarmlingClaw extends IntrinsicWeapon {

    /** Base damage for the claw attack. Chain Strike deals DAMAGE/2. */
    public static final int DAMAGE = 8;

    /** Constructor. */
    public SwarmlingClaw() {
        super(DAMAGE, "slashes", 75, "serrated claw");
    }
}
