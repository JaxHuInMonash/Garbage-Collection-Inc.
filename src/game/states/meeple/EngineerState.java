package game.states.meeple;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TransmuteAction;
import game.actors.StatefulCreature;
import game.capabilities.Ability;
import game.capabilities.Transmutable;
import game.states.CreatureState;
import game.statuses.DizzyStatus;


/**
 * The Meeple's crafting state. Unique Behaviour 3.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Repelling Pulse — all adjacent
 * actors that are <em>not</em> Meeples receive {@link DizzyStatus}(1), causing
 * them to skip their next turn.
 * <p>
 * <strong>Unique action:</strong> Transmute — converts an adjacent or current-tile
 * {@link Transmutable} item into {@link game.items.RefinedMaterial}.
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link NomadState}: no Scrap items on any adjacent or current tile.</li>
 *   <li>→ {@link OverloadedState}: more than 3 Undead actors are adjacent.</li>
 * </ul>
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class EngineerState implements CreatureState {

    private static final int OVERLOAD_UNDEAD_THRESHOLD = 3;

    @Override
    public void onEnter(StatefulCreature creature, GameMap map) {
        Location loc = map.locationOf(creature);
        for (Exit exit : loc.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor()) continue;
            Actor actor = dest.getActor();
            if (!(actor.hasAbility(Ability.IS_MEEPLE))) {
                actor.addStatus(new DizzyStatus(1));
            }
        }
    }

    @Override
    public CreatureState checkTransitions(StatefulCreature creature, Location location, GameMap map) {
        // → Overloaded if surrounded by more than THRESHOLD Undead
        int adjacentUndead = 0;
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.containsAnActor() && dest.getActor().hasAbility(Ability.IS_UNDEAD)) {
                adjacentUndead++;
            }
        }
        if (adjacentUndead > OVERLOAD_UNDEAD_THRESHOLD) {
            return new OverloadedState();
        }

        // → Nomad if nothing useful remains nearby.
        // Scrap alone (e.g. ScrapPile) is not enough to stay — there must be at
        // least one Transmutable item the Meeple can actually convert, otherwise
        // it would wander aimlessly in EngineerState with nothing to do.
        if (hasTransmutableNearby(location)) return this;
        return new NomadState();
    }

    @Override
    public Action getAction(StatefulCreature creature, Location location) {
        // Prefer Transmute if a Transmutable item is on the current or an adjacent tile
        if (!location.getItemsAs(Transmutable.class).isEmpty()) {
            return new TransmuteAction(location);
        }
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.getItemsAs(Transmutable.class).isEmpty()) {
                return new TransmuteAction(dest);
            }
        }
        return wanderOrDoNothing(creature, location);
    }

    @Override
    public String getStateName() {
        return "Engineer";
    }

    /** Returns true if a {@link Transmutable} item exists on the current or any adjacent tile. */
    private boolean hasTransmutableNearby(Location location) {
        if (!location.getItemsAs(Transmutable.class).isEmpty()) return true;
        for (Exit exit : location.getExits()) {
            if (!exit.getDestination().getItemsAs(Transmutable.class).isEmpty()) return true;
        }
        return false;
    }
}