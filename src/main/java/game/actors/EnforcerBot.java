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
/**
 * An Actor dynamically spawned by {@link game.penalty.InfractionManager} to
 * pursue a violating {@link ContractedWorker}.
 * <p>
 * Each turn, cycles through three behaviours in priority order:
 * <ol>
 *   <li>{@link SurrenderDetectBehaviour} — despawn if target is gone</li>
 *   <li>{@link ChaseAndFineAction} — chase and fine the target</li>
 *   <li>{@link PatrolBehaviour} — wander randomly when idle</li>
 * </ol>
 * Carries {@link Ability#CAN_PASS_DOORS} to pass through
 * {@link game.grounds.SecuredDoor} tiles regardless of lock state.
 *
 * @author Zhengjun Yang
 */
public class EnforcerBot extends Actor {
    private final SurrenderDetectBehaviour surrenderDetect;
    private final ChaseAndFineAction chaseAndFine;
    private final PatrolBehaviour patrol;
    /**
     * Constructor.
     *
     * @param target the ContractedWorker this bot is pursuing
     */
    public EnforcerBot(Actor target) {
        super("EnforcerBot", 'E', 999, new BasicInventory());
        this.surrenderDetect = new SurrenderDetectBehaviour(target);
        this.chaseAndFine = new ChaseAndFineAction(target);
        this.patrol = new PatrolBehaviour(target);
        this.enableAbility(Ability.CAN_PASS_DOORS);
    }
    /**
     * Runs the three behaviours in priority order each turn.
     *
     * @param actions    the list of allowable actions (unused)
     * @param lastAction the action performed last turn
     * @param map        the current game map
     * @param display    the display (unused)
     * @return the action to perform this turn
     */
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

