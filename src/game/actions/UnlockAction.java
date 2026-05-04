package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Unlockable;

/**
 * The bureaucratic process of asking a piece of the environment for permission to pass.
 * This action unlocks a specific door at a known location.
 *
 * @author Jax
 */
public class UnlockAction extends Action {
    private final Unlockable unlockable;

    /**
     * Constructor.
     */
    public UnlockAction(Unlockable unlockable) {
        this.unlockable = unlockable;
    }

    /**
     * Unlocks the target door.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return unlockable.unlock(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks " + unlockable;
    }
}
