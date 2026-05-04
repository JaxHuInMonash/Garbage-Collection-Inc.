package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UseMagicCircleAction;

public class MagicCircle extends Ground {

    public MagicCircle() {
        super('◎', "Magic Circle");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new UseMagicCircleAction(location, direction));
        return actions;
    }
}