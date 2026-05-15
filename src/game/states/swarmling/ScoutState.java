package game.states.swarmling;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.capabilities.Ability;
import game.items.PheromoneTrail;
import game.states.CreatureState;

/**
 * The Swarmling's scouting state.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Drops a {@link PheromoneTrail}
 * item on the current tile (Workers can consume it for +5 HP).
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link CullingState}: any Parasite detected in an adjacent tile.</li>
 *   <li>→ {@link BerserkState}: the Swarmling took external damage (combat hit)
 *       while no adjacent Parasite is present.</li>
 * </ul>
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class ScoutState implements CreatureState {

    @Override
    public void onEnter(StatefulCreature creature, GameMap map) {
        Location loc = map.locationOf(creature);
        loc.addItem(new PheromoneTrail());
    }

    @Override
    public CreatureState checkTransitions(StatefulCreature creature, Location location, GameMap map) {
        // Check for adjacent Parasites first
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.containsAnActor() && dest.getActor().hasAbility(Ability.IS_PARASITE)) {
                return new CullingState();
            }
        }

        // If the creature took external damage (not passive drain) → Berserk
        if (creature.consumeExternalDamageFlag()) {
            return new BerserkState();
        }

        return this;
    }

    @Override
    public Action getAction(StatefulCreature creature, Location location) {
        return wanderOrDoNothing(creature, location);
    }

    @Override
    public String getStateName() {
        return "Scout";
    }
}