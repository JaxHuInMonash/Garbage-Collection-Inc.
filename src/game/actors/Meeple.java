package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.BasicInventory;
import game.states.CreatureState;
import game.states.meeple.NomadState;
import game.statuses.DizzyStatus;
import game.weapons.MeepleFist;

/**
 * A compact, friendly support automaton designed to assist Workers.
 * <p>
 * Display character: {@code Ψ}. Starting HP: 40.
 * <p>
 * The Meeple uses a formal State Pattern. Its four states are:
 * {@link game.states.meeple.NomadState},
 * {@link game.states.meeple.EngineerState},
 * {@link game.states.meeple.GuardianState}, and
 * {@link game.states.meeple.OverloadedState}.
 * <p>
 * When {@link DizzyStatus} is applied by an EngineerState Repelling Pulse,
 * the Meeple skips its action for that turn.
 */
public class Meeple extends Actor {

    private CreatureState currentState;
    private boolean initialEntryDone = false;

    /** Constructor. */
    public Meeple() {
        super("Meeple", 'Ψ', 40, new BasicInventory());
        this.setIntrinsicWeapon(new MeepleFist());
        this.currentState = new NomadState();
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) return new DoNothingAction();

        // Dizzy check — skip turn if disoriented
        if (hasStatus(DizzyStatus.class)) return new DoNothingAction();

        Location location = map.locationOf(this);

        // Fire onEnter for the initial state (first turn only)
        if (!initialEntryDone) {
            currentState.onEnter(this, map);
            initialEntryDone = true;
        }

        // Deterministic state transition
        CreatureState nextState = currentState.checkTransitions(this, location, map);
        if (nextState != currentState) {
            currentState.onExit(this, map);
            currentState = nextState;
            currentState.onEnter(this, map);
        }

        display.println(this + " [" + currentState.getStateName() + "]");
        return currentState.getAction(this, location);
    }

    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        return new ActionList();
    }
}
