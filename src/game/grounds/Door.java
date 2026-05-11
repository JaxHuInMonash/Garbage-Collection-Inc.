package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ItemStatistics;
import game.actions.UnlockAction;
import game.capabilities.Ability;
import game.capabilities.Unlockable;

/**
 * An abstract Door class that is intended to be extended,
 * providing varying clearances and overriding where necessary
 *
 * @author Tommy
 */
public abstract class Door extends Ground implements Unlockable {
    private boolean isUnlocked = false;

    private final int requiredClearance;

    public Door(Character displayChar, String name, int requiredClearance) {
        super(displayChar, name);

        this.requiredClearance = requiredClearance;
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

        if (!isUnlocked && hasClearance(actor))
            actions.add(new UnlockAction(this));

        return actions;
    }

    /**
     * whether the actor has the required level of clearance of
     * the door, based on the items in their backpack.
     *
     * @param actor to check for clearance
     * @return whether the actor has the required level of clearance
     */
    private boolean hasClearance(Actor actor) {
        for (Item item : actor.getInventory().getItems())
            if (item.hasStatistic(ItemStatistics.CLEARANCE) && item.getStatistic(ItemStatistics.CLEARANCE) >= requiredClearance)
                return true;

        return false;
    }
}
