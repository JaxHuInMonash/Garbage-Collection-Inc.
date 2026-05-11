package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UnlockAction;
import game.capabilities.Ability;
import game.capabilities.Unlockable;

/**
 * Its primary purpose in the universe is to halt the progress of underpaid
 * contracted workers until they can produce the correct rectangular
 * piece of plastic.
 * <p>
 * A door starts locked and can be unlocked by an actor with ACCESS_LEVEL_2.
 *
 * @author Jax
 * @author Tommy
 */
public class IronDoor extends Door {
    private static final int REQUIRED_CLEARANCE = 2;

    /**
     * Constructor.
     */
    public IronDoor() {
        super('N', "Iron Door", REQUIRED_CLEARANCE);
    }
}
