package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.BasicInventory;
import game.behaviours.InfectBehaviour;
import game.behaviours.WanderBehaviour;

import java.util.Map;
import java.util.TreeMap;

/**
 * A parasitic creature that wanders in search of hosts to infect.
 * <p>
 * The Parasite has 30 hit points and is displayed as 'x'. It does not attack
 * directly; instead, it searches adjacent tiles for {@link game.capabilities.Infectable}
 * entities (actors or items) and infects the first one found. After a successful
 * infection, the Parasite dies.
 * <p>
 * Behaviours are prioritised: infect first, wander second.
 *
 * @author Jax
 */
public class Parasite extends Actor {

    /** Prioritised map of behaviours this Parasite can perform (lower key = higher priority). */
    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /**
     * Constructor.
     */
    public Parasite() {
        super("Parasite", 'x', 30, new BasicInventory());
        this.behaviours.put(1, new InfectBehaviour());
        this.behaviours.put(2, new WanderBehaviour());
    }

    /**
     * Each turn, the Parasite checks its behaviours in priority order.
     * It infects an adjacent target if one is available, otherwise wanders
     * randomly. If no behaviour can act, it does nothing.
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
