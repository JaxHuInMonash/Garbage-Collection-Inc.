package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.BasicInventory;
import game.behaviours.ConsumeItemBehaviour;
import game.behaviours.WanderBehaviour;

import java.util.Map;
import java.util.TreeMap;

/**
 * A non-hostile, gluttonous slime that wanders the moon's facility.
 * <p>
 * The Slime has 25 hit points and cannot attack. Instead, it wanders the map
 * and consumes any {@link game.capabilities.Consumable} items it finds on the
 * ground. The same consumption effects that apply to workers also apply to
 * the Slime (e.g., poison from a rotten apple).
 * <p>
 * Behaviours are prioritised: consume items first, wander second.
 *
 * @author Jax
 */
public class Slime extends Actor {

    /** Prioritised map of behaviours this Slime can perform (lower key = higher priority). */
    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /**
     * Constructor.
     */
    public Slime() {
        super("Slime", '⍾', 25, new BasicInventory());
        this.behaviours.put(1, new ConsumeItemBehaviour());
        this.behaviours.put(2, new WanderBehaviour());
    }

    /**
     * Each turn, the Slime checks its behaviours in priority order.
     * It consumes a ground item if one is available, otherwise wanders randomly.
     * If no behaviour can act, it does nothing.
     *
     * @param actions    collection of possible Actions (unused for NPCs)
     * @param lastAction the Action this Actor took last turn
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the action to perform this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            return new DoNothingAction();
        }

        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }
}
