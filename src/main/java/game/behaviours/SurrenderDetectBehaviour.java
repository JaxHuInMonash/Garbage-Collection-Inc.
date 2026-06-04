package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ChaseAndFineAction;

public class SurrenderDetectBehaviour extends EnforcerBehaviour {

    public SurrenderDetectBehaviour(Actor target) { super(target); }

    @Override
    public Action operate(Actor bot, Location location) {
        GameMap map = location.map();
        if (!map.contains(target) || !target.isConscious())
            return new ChaseAndFineAction.DespawnAction(bot, target);
        return null;
    }
}
