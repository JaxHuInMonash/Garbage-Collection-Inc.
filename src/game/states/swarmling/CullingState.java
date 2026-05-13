package game.states.swarmling;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.DevourAction;
import game.behaviours.WanderBehaviour;
import game.capabilities.Ability;
import game.states.CreatureState;

import java.util.List;

/**
 * The Swarmling's Parasite-hunting state. Unique Behaviour 1.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Screech — all Parasites within
 * 8 tiles are pulled 2 tiles closer to the Swarmling.
 * <p>
 * <strong>Unique action:</strong> Devour — consumes an adjacent Parasite
 * actor, removing it from the map and healing 10% of the Swarmling's max HP.
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link BerserkState}: a {@link Ability#HOSTILE_TO_WORKERS} actor is
 *       detected within 3 tiles (proxy for a threat to a nearby Worker).</li>
 *   <li>→ {@link DormantState}: no Parasites remain within 3 tiles.</li>
 * </ul>
 */
public class CullingState implements CreatureState {

    private static final int SCREECH_RADIUS = 8;
    private static final int PULL_STEPS = 2;
    private static final int BERSERK_RADIUS = 3;
    private static final int PARASITE_CHECK_RADIUS = 3;

    @Override
    public void onEnter(Actor creature, GameMap map) {
        Location swarmlingLoc = map.locationOf(creature);
        // Screech: pull all Parasites within SCREECH_RADIUS two tiles closer
        for (Location nearby : List.copyOf(swarmlingLoc.getNearbyLocations(SCREECH_RADIUS))) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.IS_PARASITE)) {
                pullActorCloser(nearby.getActor(), swarmlingLoc, map, PULL_STEPS);
            }
        }
    }

    @Override
    public CreatureState checkTransitions(Actor creature, Location location, GameMap map) {
        // → Berserk if a hostile non-Parasite threat is in range (proxy for Worker under attack)
        for (Location nearby : location.getNearbyLocations(BERSERK_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            Actor actor = nearby.getActor();
            if (actor.hasAbility(Ability.HOSTILE_TO_WORKERS)) {
                return new BerserkState();
            }
        }

        // → Dormant if no Parasites nearby
        boolean parasiteNearby = false;
        for (Location nearby : location.getNearbyLocations(PARASITE_CHECK_RADIUS)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.IS_PARASITE)) {
                parasiteNearby = true;
                break;
            }
        }
        if (!parasiteNearby) {
            return new DormantState();
        }

        return this;
    }

    @Override
    public Action getAction(Actor creature, Location location) {
        // Prefer Devour if an adjacent Parasite exists
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.containsAnActor() && dest.getActor().hasAbility(Ability.IS_PARASITE)) {
                return new DevourAction(dest.getActor());
            }
        }
        Action wander = new WanderBehaviour().operate(creature, location);
        return wander != null ? wander : new DoNothingAction();
    }

    @Override
    public String getStateName() {
        return "Culling";
    }

    /** Moves {@code actor} up to {@code steps} tiles toward {@code target}. */
    private void pullActorCloser(Actor actor, Location target, GameMap map, int steps) {
        for (int i = 0; i < steps; i++) {
            if (!map.contains(actor)) return;
            Location current = map.locationOf(actor);
            Location best = null;
            double bestDist = euclidean(current, target);

            for (Exit exit : current.getExits()) {
                Location dest = exit.getDestination();
                if (!dest.canActorEnter(actor)) continue;
                double dist = euclidean(dest, target);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = dest;
                }
            }

            if (best == null) break;
            map.moveActor(actor, best);
        }
    }

    private double euclidean(Location a, Location b) {
        int dx = a.x() - b.x();
        int dy = a.y() - b.y();
        return Math.sqrt((double) dx * dx + dy * dy);
    }
}
