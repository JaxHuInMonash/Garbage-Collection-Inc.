package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.BasicInventory;
import game.behaviours.AttackBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.Ability;
import game.statuses.DizzyStatus;
import game.weapons.DroneLaser;

import java.util.Map;
import java.util.TreeMap;

/**
 * An automated security drone spawned when the facility alarm is triggered.
 * <p>
 * The drone has 10 hit points and is armed with a {@link DroneLaser}
 * (3 damage, 50% hit rate). It is highly aggressive and will attack any
 * adjacent actor indiscriminately — workers and other creatures alike.
 * <p>
 * Behaviours are prioritised: attack any adjacent entity first, follow the
 * worker who triggered the alarm second, wander randomly as a fallback.
 *
 * @author Jax
 */
public class SecurityDrone extends Actor {
    private static final int INITIAL_HIT_POINTS = 10;

    /** Prioritised map of behaviours this drone can perform (lower key = higher priority). */
    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /**
     * Constructor.
     *
     * @param target the actor this drone should pursue (the alarm trigger)
     */
    public SecurityDrone(Actor target) {
        super("Security Drone", 'δ', INITIAL_HIT_POINTS, new BasicInventory());
        this.enableAbility(Ability.HOSTILE_TO_WORKERS);
        this.setIntrinsicWeapon(new DroneLaser());
        this.behaviours.put(1, new AttackBehaviour(actor -> true));
        this.behaviours.put(2, new FollowBehaviour(target));
        this.behaviours.put(3, new WanderBehaviour());
    }

    /**
     * Each turn, the drone checks its behaviours in priority order.
     * It attacks any adjacent actor first, follows the triggering worker
     * if no one is adjacent, or wanders randomly as a fallback.
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

        if (this.hasStatus(DizzyStatus.class)) {
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
