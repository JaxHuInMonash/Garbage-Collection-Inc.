package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.BasicInventory;
import game.actions.ChaseAndFineAction;
import game.behaviours.PatrolBehaviour;
import game.behaviours.SurrenderDetectBehaviour;
import game.capabilities.Ability;
public class EnforcerBot extends Actor {
    private final SurrenderDetectBehaviour surrenderDetect;
    private final ChaseAndFineAction chaseAndFine;
    private final PatrolBehaviour patrol;

    public EnforcerBot(Actor target) {
        super("EnforcerBot", 'E', 999, new BasicInventory());
        this.surrenderDetect = new SurrenderDetectBehaviour(target);
        this.chaseAndFine = new ChaseAndFineAction(target);
        this.patrol = new PatrolBehaviour(target);
        this.enableAbility(Ability.CAN_PASS_DOORS);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location loc = map.locationOf(this);
        Action s = surrenderDetect.operate(this, loc);
        if (s != null) return s;
        Action c = chaseAndFine.operate(this, loc);
        if (c != null) return c;
        Action p = patrol.operate(this, loc);
        if (p != null) return p;
        return new DoNothingAction();
    }
}

