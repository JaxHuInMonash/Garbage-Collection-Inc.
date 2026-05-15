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
     * Tracks whether this creature was damaged by an external source (another
     * actor's attack) since the last time the flag was consumed. Environmental
     * passives should use {@link #applyPassiveDamage(int)} instead of
     * {@link #hurt(int)} so this flag is not falsely triggered.
     */
    private boolean tookExternalDamage = false;

    /**
     * Constructor.
     *
     * @param name         the name of the Actor
     * @param displayChar  the character that will represent the Actor in the display
     * @param hitPoints    the Actor's starting hit points
     * @param inventory    the Actor's inventory system
     * @param initialState the state this creature begins in
     */
    public StatefulCreature(String name, char displayChar, int hitPoints, Inventory inventory, CreatureState initialState) {
        super(name, displayChar, hitPoints, inventory);

        this.currentState = initialState;
    }

    /**
     * Marks incoming damage as external (combat) and forwards to
     * {@link Actor#hurt(int)}. Subclasses that intercept damage (e.g. for
     * shields) should call {@link #markExternalDamage()} at the start of
     * their override so the flag is set even if the shield absorbs everything.
     */
    @Override
    public void hurt(int damage) {
        tookExternalDamage = true;
        super.hurt(damage);
    }

    /**
     * Sets the external-damage flag without applying damage. Called by
     * subclass {@code hurt()} overrides that handle absorption before
     * delegating to {@code super.hurt()}.
     */
    protected void markExternalDamage() {
        tookExternalDamage = true;
    }

    /**
     * Applies damage without setting the external-damage flag. Intended for
     * passive or environmental effects (e.g. Swarmling's normal-ground drain)
     * that should not be mistaken for combat hits.
     *
     * @param damage the amount of passive damage to apply
     */
    protected void applyPassiveDamage(int damage) {
        super.hurt(damage);
    }

    /**
     * Returns and clears the external-damage flag. States like ScoutState
     * call this to check whether the creature was attacked since the last turn.
     *
     * @return true if external damage was received
     */
    public boolean consumeExternalDamageFlag() {
        boolean result = tookExternalDamage;
        tookExternalDamage = false;
        return result;
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

    /**
     * Hook for environmental or per-turn passive effects (e.g. Swarmling's toxic
     * healing / normal-ground drain). Runs each turn before state transitions.
     * If a non-null Action is returned, it replaces the state's action for this turn.
     *
     * @param map the current game map
     * @return an Action to execute instead of the state's action, or null for normal flow
     */
    public Action passive(GameMap map) { return null; }
}
