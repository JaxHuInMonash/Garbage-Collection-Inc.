package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.states.CreatureState;
import game.statuses.DizzyStatus;

/**
 * The Abstract class that stateful creatures extend from,
 * allows creatures to alternate between states!!
 *
 * @author Ethan
 * @author Tommy
 */
public abstract class StatefulCreature extends Actor {
    private CreatureState currentState;
    private boolean initialEntryDone = false;

    /**
     * The constructor of the Actor class.
     *
     * @param name        the name of the Actor
     * @param displayChar the character that will represent the Actor in the
     *                    display
     * @param hitPoints   the Actor's starting hit points
     * @param inventory   the Actor's inventory system
     */
    public StatefulCreature(String name, char displayChar, int hitPoints, Inventory inventory, CreatureState initialState) {
        super(name, displayChar, hitPoints, inventory);

        this.currentState = initialState;
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious() || skipTurn(map)) return new DoNothingAction();

        Location location = map.locationOf(this);

        // Fire onEnter for the initial state (first turn only)
        if (!initialEntryDone) {
            currentState.onEnter(this, map);
            initialEntryDone = true;
        }

        Action passiveAction = passive(map);
        if (passiveAction != null)
            return passiveAction;

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

    /**
     * Checks whether the creature should skip its turn. By default, skips
     * when a {@link DizzyStatus} is active. Subclasses may override to add
     * additional skip conditions.
     *
     * @param map the current game map
     * @return true if this creature should do nothing this turn
     */
    public boolean skipTurn(GameMap map) { return hasStatus(DizzyStatus.class); }

    public Action passive(GameMap map) { return null; }
}
