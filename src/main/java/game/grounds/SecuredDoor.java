package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UnlockDoorAction;
import game.ItemStatistics;
import game.capabilities.Ability;
/**
 * An abstract base class representing a tiered security door in the facility.
 * All doors require an {@link game.items.AccessCard} of a minimum clearance level
 * to unlock. Each subclass defines its own required clearance level
 * and {@link #unlockEffect(Actor, Location)}.
 * <p>
 * This design centralises common door logic (clearance checking, unlock action)
 * in one place, following the DRY and Open/Closed principles.
 * <p>
 * actors with the {@link Ability#CAN_PASS_DOORS} capability
 * can now bypass this door regardless of lock state.
 *
 * @author zhengjun Yang
 * @author Tommy
 */
public abstract class SecuredDoor extends Ground {
    private final int REQUIRED_CLEARANCE;

    private boolean isUnlocked = false;
    /**
     * Constructor.
     *
     * @param displayChar the character representing this door on the map
     * @param name        the name of this door
     */
    public SecuredDoor(char displayChar, String name, int requiredClearance) {
        super(displayChar, name);

        this.REQUIRED_CLEARANCE = requiredClearance;
    }
    /**
     * Returns true if the actor can enter this door.
     * <p>
     * actors carrying the {@link Ability#CAN_PASS_DOORS} capability
     * (such as {@link game.actors.EnforcerBot}) can pass through regardless of
     * lock state, allowing them to pursue violating Workers across the facility
     * without being blocked by security doors.
     *
     * @param actor the actor attempting to enter
     * @return true if the actor has CAN_PASS_DOORS ability or the door is unlocked
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        if (actor.hasAbility(Ability.CAN_PASS_DOORS)) return true;
        return isUnlocked;
    }
    /**
     * Defines the side effect that occurs when this door is unlocked.
     *
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of the unlock effect
     */
    public abstract String unlockEffect(Actor actor, Location location);
    /**
     * Unlocks this door and applies the unlock side effect.
     *
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of what happened
     */
    public String unlock(Actor actor, Location location) {
        this.isUnlocked = true;
        return unlockEffect(actor, location);
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!isUnlocked && hasRequiredClearance(actor)) {
            actions.add(new UnlockDoorAction(this, location, direction));
        }
        return actions;
    }

    private boolean hasRequiredClearance(Actor actor) {
        return actor.getInventory().getItems().stream()
                .filter(item -> item.hasAbility(Ability.CAN_UNLOCK_DOORS))
                .anyMatch(item -> item.getStatistic(ItemStatistics.CLEARANCE) >= REQUIRED_CLEARANCE);
    }
}