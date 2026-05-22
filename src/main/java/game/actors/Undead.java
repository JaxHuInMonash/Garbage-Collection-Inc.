package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.BasicInventory;
import game.alarm.AlarmListener;
import game.alarm.AlarmManager;
import game.behaviours.AttackBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.Ability;
import game.capabilities.Infectable;
import game.items.UndeadCorpse;
import game.statuses.DizzyStatus;
import game.weapons.UndeadFist;

import java.util.Map;
import java.util.TreeMap;

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
public class Undead extends Actor implements AlarmListener, Infectable {
    private static final int INITIAL_HIT_POINTS = 15;

    /** Prioritised map of behaviours this Undead can perform (lower key = higher priority). */
    private final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /**
     * Constructor. Registers this Undead with the {@link AlarmManager} so
     * it will be notified if the facility alarm is triggered.
     */
    public Undead() {
        super("Undead", 'Ѫ', INITIAL_HIT_POINTS, new BasicInventory());
        this.enableAbility(Ability.IS_UNDEAD);
        this.enableAbility(Ability.HOSTILE_TO_WORKERS);
        this.setIntrinsicWeapon(new UndeadFist());
        this.behaviours.put(1, new AttackBehaviour());
        this.behaviours.put(3, new WanderBehaviour());
        AlarmManager.getInstance().registerListener(this);
    }

    /**
     * Responds to the facility alarm by injecting a {@link FollowBehaviour}
     * targeting the triggering worker at priority 2 (between attack and wander),
     * so the Undead will:
     * 1. Attack if adjacent to a worker (priority 1)
     * 2. Follow the triggering worker if not adjacent (priority 2)
     * 3. Wander randomly only if following fails (priority 3)
     *
     * @param triggeringWorker the worker who triggered the alarm
     * @param map              the game map
     */
    @Override
    public void onAlarmTriggered(Actor triggeringWorker, GameMap map) {
        this.behaviours.put(2, new FollowBehaviour(triggeringWorker));
    }

    /**
     * Handles infection by a Parasite. Alien biology severely rejects the
     * reanimated dead — the Undead immediately blows up and is killed.
     * No status is added since the effect is instantaneous.
     *
     * @param infector the Parasite that caused the infection
     * @param map      the current game map
     */
    @Override
    public void onInfected(Actor infector, GameMap map) {
        this.unconscious(map);
    }

    /**
     * No-op: the Undead dies instantly on infection, so this is never called.
     *
     * @param location the current location (unused)
     */
    @Override
    public void onInfectionTick(Location location) {
        // Instant death — no per-turn infection behaviour
    }

    /**
     * Always false: the Undead dies instantly on infection, so no ongoing
     * infection status is needed.
     *
     * @return false always
     */
    @Override
    public boolean isInfectionActive() {
        return false;
    }

    /**
     * Drops an {@link UndeadCorpse} on this tile before removal, so the
     * Meeple's EngineerState can detect and transmute the remains.
     */
    @Override
    public String unconscious(Actor otherActor, GameMap map) {
        map.locationOf(this).addItem(new UndeadCorpse());
        return super.unconscious(otherActor, map);
    }

    @Override
    public String unconscious(GameMap map) {
        map.locationOf(this).addItem(new UndeadCorpse());
        return super.unconscious(map);
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