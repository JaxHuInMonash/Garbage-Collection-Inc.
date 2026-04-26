package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.BasicInventory;
import game.alarm.AlarmListener;
import game.alarm.AlarmManager;
import game.behaviours.AttackBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.weapons.UndeadFist;

import java.util.ArrayList;
import java.util.List;

/**
 * A hostile reanimated corpse that wanders the moon's facility.
 * <p>
 * The Undead has 15 hit points and attacks nearby workers with its bare fist
 * (1 damage, 10% hit rate). It completely ignores other creatures.
 * Behaviours are prioritised: attack first, wander second.
 * <p>
 * When the facility alarm is triggered, the Undead responds by injecting a
 * {@link FollowBehaviour} at highest priority, causing it to hunt down the
 * worker who triggered the alarm. This overrides wandering while still
 * allowing the attack behaviour to take precedence when adjacent to the target.
 *
 * @author Jax
 */
public class Undead extends Actor implements AlarmListener {

    /** Prioritised list of behaviours this Undead can perform. */
    private final List<Behaviour<Actor, Action>> behaviours = new ArrayList<>();

    /**
     * Constructor. Registers this Undead with the {@link AlarmManager} so
     * it will be notified if the facility alarm is triggered.
     */
    public Undead() {
        super("Undead", 'Ѫ', 15, new BasicInventory());
        this.setIntrinsicWeapon(new UndeadFist());
        this.behaviours.add(new AttackBehaviour());
        this.behaviours.add(new WanderBehaviour());
        AlarmManager.getInstance().registerListener(this);
    }

    /**
     * Responds to the facility alarm by injecting a {@link FollowBehaviour}
     * targeting the triggering worker. The follow behaviour is inserted
     * between attack (index 0) and wander (index 1), so the Undead will:
     * 1. Attack if adjacent to a worker
     * 2. Follow the triggering worker if not adjacent
     * 3. Wander randomly only if following fails
     *
     * @param triggeringWorker the worker who triggered the alarm
     * @param map              the game map
     */
    @Override
    public void onAlarmTriggered(Actor triggeringWorker, GameMap map) {
        this.behaviours.add(1, new FollowBehaviour(triggeringWorker));
    }

    /**
     * Each turn, the Undead checks its behaviours in priority order.
     * It attacks an adjacent worker if one exists, follows the alarm target
     * if the alarm has been triggered, otherwise wanders randomly.
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

        for (Behaviour<Actor, Action> behaviour : behaviours) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }
}
