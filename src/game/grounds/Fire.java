package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.BurnStatus;
import game.capabilities.Ability;
/**
 * A fire ground tile spawned by a leaking Lantern. Displays as {@code ^}.
 * <p>
 * The fire lasts for a set number of turns (default 5). Any actor standing
 * on a fire tile receives a {@link BurnStatus} (1 damage per turn for 5 turns).
 * When the fire expires, the ground reverts to whatever was there before.
 *
 * @author Jax
 */
public class Fire extends Ground {

    private static final int FIRE_DURATION = 5;
    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 5;

    private final Ground originalGround;
    private int remainingTurns;

    /**
     * Constructor.
     *
     * @param originalGround the ground type that was here before the fire started
     */

    public Fire(Ground originalGround) {
        this(originalGround, FIRE_DURATION);
    }
    public Fire(Ground originalGround, int duration) {
        super('^', "Fire");
        this.enableAbility(Ability.ON_FIRE);
        this.originalGround = originalGround;
        this.remainingTurns = duration;

    }

    /**
     * Returns a string representation including remaining turns,
     * since the fire's remaining duration is meaningful gameplay state.
     *
     * @return the fire's name and remaining turns
     */
    @Override
    public String toString() {
        return "Fire (" + remainingTurns + " turns remaining)";
    }

    /**
     * Fire does not block actor entry; actors can walk into fire
     * (and suffer for it).
     *
     * @param actor the Actor to check
     * @return true always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Each turn, the fire applies a BurnStatus to any actor standing on it
     * and decrements its remaining duration. When expired, reverts the ground
     * to its original type.
     *
     * @param location the location of this fire tile
     */
    @Override
    public void tick(Location location) {
        // Apply burn to any actor standing on this fire
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.addStatus(new BurnStatus(BURN_DAMAGE, BURN_DURATION));
        }

        remainingTurns--;
        if (remainingTurns <= 0) {
            location.setGround(originalGround);
        }
    }
}
