package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UseMagicCircleAction;
import game.capabilities.Ability;
/**
 * A magical marking found inside 20-overflow. Displays as {@code ◎}.
 * <p>
 * Magic Circles allow within-map teleportation. Using one randomly teleports
 * the worker to one of the other magic circles on the map. The magical energy
 * surges upon arrival, instantly spawning a Flask on an empty tile directly
 * next to the destination circle.
 *
 * @author zhengjun Yang
 */
public class MagicCircle extends Ground {
    /**
     * Constructor.
     */
    public MagicCircle() {
        super('◎', "Magic Circle");
        this.enableAbility(Ability.IS_MAGIC_CIRCLE);
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