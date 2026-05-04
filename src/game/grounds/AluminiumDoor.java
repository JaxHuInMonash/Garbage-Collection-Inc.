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
 * A door starts locked and can be unlocked by an actor carrying an AccessCard.
 *
 * @author Jax
 */
public class AluminiumDoor extends Ground implements Unlockable {
    private boolean isUnlocked = false;

    public AluminiumDoor() {
        super('=', "Aluminium Door");
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
    public String unlock(Actor actor, GameMap map) {
        this.isUnlocked = true;

        return actor + " unlocked " + this;
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

        if (!isUnlocked && actor.hasAbility(Ability.ACCESS_LEVEL_1))
            actions.add(new UnlockAction(this));

        return actions;
    }
}
