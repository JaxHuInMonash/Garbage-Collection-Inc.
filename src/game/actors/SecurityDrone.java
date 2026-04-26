package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.BasicInventory;
import game.behaviours.AggressiveAttackBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.weapons.DroneLaser;

import java.util.ArrayList;
import java.util.List;

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

    /** Prioritised list of behaviours this drone can perform. */
    private final List<Behaviour<Actor, Action>> behaviours = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param target the actor this drone should pursue (the alarm trigger)
     */
    public SecurityDrone(Actor target) {
        super("Security Drone", 'δ', 10, new BasicInventory());
        this.setIntrinsicWeapon(new DroneLaser());
        this.behaviours.add(new AggressiveAttackBehaviour());
        this.behaviours.add(new FollowBehaviour(target));
        this.behaviours.add(new WanderBehaviour());
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

        for (Behaviour<Actor, Action> behaviour : behaviours) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }
}
