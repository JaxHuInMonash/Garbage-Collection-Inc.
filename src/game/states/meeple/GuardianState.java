package game.states.meeple;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.KineticLinkAction;
import game.actors.StatefulCreature;
import game.capabilities.Ability;
import game.states.CreatureState;
import game.statuses.KineticLinkStatus;
import game.statuses.ShieldStatus;
import game.utils.LocationUtils;

/**
 * The Meeple's protective state. Unique Behaviour 4.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Nanobot Injection — all Workers
 * within 3 tiles receive a {@link ShieldStatus} absorbing 30 HP.
 * <p>
 * <strong>Unique action:</strong> Kinetic Link — the Meeple tethers to the
 * nearest Worker via {@link KineticLinkStatus}, forwarding 50% of each tick's
 * damage to itself. Once all tether capacity is spent the Meeple transitions
 * to Overloaded.
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link NomadState}: no Workers in the 3-tile radius.</li>
 *   <li>→ {@link OverloadedState}: the shieldBattery counter reaches 0
 *       (all link applications used up).</li>
 * </ul>
 */
public class GuardianState implements CreatureState {

    private static final int NANOBOT_RADIUS = 3;
    private static final int SHIELD_HP = 30;
    private static final int INITIAL_BATTERY = 3;

    private int shieldBattery = INITIAL_BATTERY;

    @Override
    public void onEnter(StatefulCreature creature, GameMap map) {
        Location loc = map.locationOf(creature);
        for (Location nearby : loc.getNearbyLocations(NANOBOT_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            Actor actor = nearby.getActor();
            if (actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                actor.addStatus(new ShieldStatus(SHIELD_HP));
            }
        }
    }

    @Override
    public void onExit(StatefulCreature creature, GameMap map) {
        // No per-state cleanup needed; KineticLinkStatus auto-expires when Meeple dies
    }

    @Override
    public CreatureState checkTransitions(StatefulCreature creature, Location location, GameMap map) {
        if (shieldBattery <= 0) {
            return new OverloadedState();
        }

        boolean workerNearby = false;
        for (Location nearby : location.getNearbyLocations(NANOBOT_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            if (nearby.getActor().hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                workerNearby = true;
                break;
            }
        }
        if (!workerNearby) {
            return new NomadState();
        }

        return this;
    }

    @Override
    public Action getAction(StatefulCreature creature, Location location) {
        if (shieldBattery <= 0) {
            return wanderOrDoNothing(creature, location);
        }

        // Find the nearest untethered Worker within range
        Actor nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Location nearby : location.getNearbyLocations(NANOBOT_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            Actor actor = nearby.getActor();
            if (!actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) continue;
            if (actor.hasStatus(KineticLinkStatus.class)) continue; // already tethered
            double dist = LocationUtils.euclidean(location, nearby);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = actor;
            }
        }

        if (nearest != null) {
            shieldBattery--;
            return new KineticLinkAction(nearest);
        }

        return wanderOrDoNothing(creature, location);
    }

    @Override
    public String getStateName() {
        return "Guardian";
    }

}
