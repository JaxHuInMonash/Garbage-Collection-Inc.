package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The Swarmling's CullingState unique action.
 * <p>
 * The Swarmling devours an adjacent Parasite: it is removed from the map
 * and the Swarmling heals for 10% of its maximum HP. Consuming a Parasite
 * does not consume the Swarmling's attack turn — this action IS the turn.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class DevourAction extends Action {

    private final Actor target;

    /**
     * Constructor.
     *
     * @param target the actor to devour (must have IS_PARASITE ability)
     */
    public DevourAction(Actor target) {
        this.target = target;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        int maxHp = actor.getMaximumStatistic(ActorStatistics.HEALTH);
        int healAmount = Math.max(1, maxHp / 10);

        // Use the engine's death lifecycle so item drops, spawn tracking,
        // and any other death-related effects fire correctly.
        String deathResult = target.unconscious(actor, map);

        actor.heal(healAmount);
        return actor + " devours " + target + " and recovers " + healAmount + " HP.\n" + deathResult;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " devours " + target;
    }
}
