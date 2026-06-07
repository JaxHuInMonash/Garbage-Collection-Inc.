package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A minimal concrete {@link Actor} for tests, used when mocking {@code Actor}
 * isn't viable because {@link Actor#hasAbility(Enum)} is {@code final}
 */
public class TestActor extends Actor {

    public TestActor() {
        super("test", '@', 100, new WeightLimitedInventory(50));
    }

    public TestActor(Inventory inventory) {
        super("test", '@', 100, inventory);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        return null;
    }
}
