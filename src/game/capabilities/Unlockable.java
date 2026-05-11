package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The interface for any object that can be unlocked by an Actor
 *
 * @author Tommy
 */
public interface Unlockable {
    /**
     * Defines what happens when this Unlockable is unlocked by an actor.
     * The implementation handles all effects (healing, poisoning, removal
     * from inventory, etc.).
     *
     * @param actor actor that unlocked the Unlockable
     * @param map map the Unlockable was unlocked on
     * @return resultant message of unlocking the Unlockable
     */
    String unlock(Actor actor, GameMap map);
}
