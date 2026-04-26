package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UnlockDoorAction;
import game.capabilities.Ability;

/**
 * Its primary purpose in the universe is to halt the progress of underpaid
 * contracted workers until they can produce the correct rectangular
 * piece of plastic.
 * <p>
 * A door starts locked and can be unlocked by an actor carrying an
 * {@link game.items.AccessCard} (which grants {@link Ability#CAN_UNLOCK_DOORS}).
 *
 * @author Jax
 */
public class Door extends Ground {
    private boolean isUnlocked = false;

    public Door() {
        super('=', "Door");
    }

    /**
     * If the door is unlocked, any actor can step through it.
     *
     * @param actor the Actor to check
     * @return true if the door is unlocked, false otherwise
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked;
    }

    /**
     * Unlocks this door, allowing all actors to pass through it.
     */
    public void unlock() {
        this.isUnlocked = true;
    }

    /**
     * If the door is locked and the actor has the ability to unlock doors,
     * provide an UnlockDoorAction targeting this specific door.
     *
     * @param actor     the Actor standing nearby
     * @param location  the location of this door
     * @param direction the direction of the door from the actor
     * @return a list of allowable actions for this door
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!isUnlocked && actor.hasAbility(Ability.CAN_UNLOCK_DOORS)) {
            actions.add(new UnlockDoorAction(this, location, direction));
        }
        return actions;
    }
}
