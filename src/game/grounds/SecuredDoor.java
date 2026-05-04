package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UnlockDoorAction;
import game.items.AccessCard;

public abstract class SecuredDoor extends Ground {

    private boolean isUnlocked = false;

    public SecuredDoor(char displayChar, String name) {
        super(displayChar, name);
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked;
    }

    protected abstract int requiredClearance();

    public abstract String unlockEffect(Actor actor, Location location);

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
                .anyMatch(item -> {
                    if (item instanceof AccessCard card) {
                        return card.getClearanceLevel() >= requiredClearance();
                    }
                    return false;
                });
    }
}