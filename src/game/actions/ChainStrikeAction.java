package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.KillListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The Swarmling's BerserkState unique attack action.
 * <p>
 * Performs a normal weapon attack against the primary target, then
 * triggers a shockwave dealing 4 damage
 * to every <em>other</em> adjacent actor. If any actor dies, the
 * {@link KillListener} is notified so BerserkState can track its kill count.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class ChainStrikeAction extends Action {

    private final Actor target;
    private final KillListener killListener;
    private static final int CHAIN_DAMAGE = 4;

    /**
     * Constructor.
     *
     * @param target       primary attack target
     * @param killListener callback notified each time a target dies
     */
    public ChainStrikeAction(Actor target, KillListener killListener) {
        this.target = target;
        this.killListener = killListener;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        StringBuilder result = new StringBuilder();

        // Primary attack
        IntrinsicWeapon weapon = actor.getIntrinsicWeapon();
        result.append(weapon.attack(actor, target, map));

        if (!target.isConscious()) {
            result.append("\n").append(target.unconscious(actor, map));
            killListener.onKill();
        }

        // Chain shockwave
        Location swarmlingLoc = map.locationOf(actor);
        List<Exit> exits = new ArrayList<>(swarmlingLoc.getExits());
        for (Exit exit : exits) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor()) continue;
            Actor adjacent = dest.getActor();
            if (adjacent == target) continue; // already hit

            adjacent.hurt(CHAIN_DAMAGE);

            result.append("\n").append(actor).append(" chain strikes ").append(adjacent)
                  .append(" for ").append(CHAIN_DAMAGE).append(" shockwave damage.");
            if (!adjacent.isConscious()) {
                result.append("\n").append(adjacent.unconscious(actor, map));
                killListener.onKill();
            }
        }

        return result.toString();
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " chain strikes " + target;
    }
}
