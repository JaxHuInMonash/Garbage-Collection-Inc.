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
import game.states.swarmling.DormantState;
import game.statuses.ReflectionStatus;
import game.weapons.SwarmlingClaw;
import game.capabilities.Ability;

import java.util.List;

/**
 * A colony fragment that thrives in toxic environments.
 * <p>
 * Display character: {@code Ŝ}. Starting HP: 50.
 * <p>
 * <strong>Environmental Passive:</strong> Loses 1 HP per turn on normal ground;
 * heals 2 HP per turn when standing on Toxic Waste (ability {@link Ability#IS_TOXIC}).
 * <p>
 * The Swarmling uses a formal State Pattern. Its four states are:
 * {@link game.states.swarmling.DormantState},
 * {@link game.states.swarmling.ScoutState},
 * {@link game.states.swarmling.CullingState}, and
 * {@link game.states.swarmling.BerserkState}.
 * <p>
 * <strong>Reflection:</strong> {@link ReflectionStatus} added by DormantState is
 * intercepted in {@link #hurt(int)} so incoming damage is absorbed before
 * reaching HP.
 */
public class Swarmling extends Actor {

    private CreatureState currentState;
    private boolean initialEntryDone = false;

    private static final int PASSIVE_DAMAGE = 1;
    private static final int TOXIC_HEAL = 2;

    /** Constructor. */
    public Swarmling() {
        super("Swarmling", 'Ŝ', 50, new BasicInventory());
        this.setIntrinsicWeapon(new SwarmlingClaw());
        this.currentState = new DormantState();
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) return new DoNothingAction();

        Location location = map.locationOf(this);

        // Fire onEnter for the initial state (first turn only)
        if (!initialEntryDone) {
            currentState.onEnter(this, map);
            initialEntryDone = true;
        }

        // Environmental passive: toxic waste heals, normal ground hurts
        if (location.getGround().hasAbility(Ability.IS_TOXIC)) {
            this.heal(TOXIC_HEAL);
        } else {
            this.hurt(PASSIVE_DAMAGE);
            if (!isConscious()) return new DoNothingAction();
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

    /**
     * Overrides hurt() to intercept damage through any active {@link ReflectionStatus}.
     * If a reflection shield is present, damage is absorbed before reaching HP.
     */
    @Override
    public void hurt(int damage) {
        List<ReflectionStatus> shields = statusesOf(ReflectionStatus.class);
        if (!shields.isEmpty()) {
            ReflectionStatus shield = shields.get(0);
            int absorbed = shield.absorb(damage);
            int remaining = damage - absorbed;
            if (!shield.isStatusActive()) {
                removeStatus(shield);
            }
            if (remaining > 0) {
                super.hurt(remaining);
            }
        } else {
            super.hurt(damage);
        }
    }

    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList list = new ActionList();
        // Swarmlings don't let other actors attack them via the menu; combat is creature-initiated
        return list;
    }
}
