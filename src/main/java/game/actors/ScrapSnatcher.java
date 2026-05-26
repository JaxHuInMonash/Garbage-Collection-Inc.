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
import game.behaviours.AttackBehaviour;
import game.behaviours.SnatchItemBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.Ability;
import game.capabilities.Infectable;
import game.statuses.DizzyStatus;
import game.statuses.InfectedStatus;
import game.weapons.ScrapSnatcherFist;

import java.util.Map;
import java.util.TreeMap;

/**
 * A scavenging creature that wanders the facility stealing depositable resources.
 * Displays as {@code s} with 25 hit points.
 * <p>
 * <b>Normal behaviour (priority order):</b>
 * <ol>
 *   <li>Snatch any {@link game.capabilities.Sellable} items on its current tile.</li>
 *   <li>Wander randomly.</li>
 * </ol>
 * <p>
 * <b>Infected (rabid) behaviour:</b> when a {@link Parasite} infects it the
 * Snatcher becomes rabid. It immediately loses the ability to snatch items and
 * instead attacks any adjacent worker with a 10%-accurate bare-claw strike for
 * 1 damage. It also takes 1 damage per turn from the infection until it dies.
 * Rabid Snatchers completely ignore other creatures.
 *
 * @author Ethan
 */
public class ScrapSnatcher extends Actor implements Infectable {

    private static final int INITIAL_HIT_POINTS = 25;

    private boolean rabid = false;

    private final Map<Integer, Behaviour<Actor, Action>> normalBehaviours = new TreeMap<>();
    private final Map<Integer, Behaviour<Actor, Action>> rabidBehaviours = new TreeMap<>();

    /** Constructor. */
    public ScrapSnatcher() {
        super("Scrap Snatcher", 's', INITIAL_HIT_POINTS, new BasicInventory());
        this.enableAbility(Ability.IS_SCRAP_SNATCHER);

        normalBehaviours.put(1, new SnatchItemBehaviour());
        normalBehaviours.put(2, new WanderBehaviour());

        // Rabid: attacks only workers (ATTACKABLE_BY_CREATURES), ignores other creatures
        rabidBehaviours.put(1, new AttackBehaviour(target -> target.hasAbility(Ability.ATTACKABLE_BY_CREATURES)));
        rabidBehaviours.put(2, new WanderBehaviour());
    }

    /**
     * Each turn, uses the rabid or normal behaviour set.
     * Infection damage (1/turn) is handled separately by {@link InfectedStatus}.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            return new DoNothingAction();
        }

        if (this.hasStatus(DizzyStatus.class)) {
            return new DoNothingAction();
        }

        Map<Integer, Behaviour<Actor, Action>> active = rabid ? rabidBehaviours : normalBehaviours;
        for (Behaviour<Actor, Action> behaviour : active.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }

    // ── Infectable ────────────────────────────────────────────────────────────

    /**
     * Parasite infection drives the Snatcher rabid: it stops hoarding and
     * immediately equips its bare claw to attack workers instead.
     */
    @Override
    public void onInfected(Actor infector, GameMap map) {
        rabid = true;
        this.setIntrinsicWeapon(new ScrapSnatcherFist());
        this.addStatus(new InfectedStatus(this));
    }

    /** Takes 1 damage per turn while rabid. */
    @Override
    public void onInfectionTick(Location location) {
        this.hurt(1);
    }

    /** Infection is active until the Snatcher dies. */
    @Override
    public boolean isInfectionActive() {
        return this.isConscious();
    }
}
