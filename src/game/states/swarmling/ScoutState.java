package game.states.swarmling;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
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
 *   <li>→ {@link BerserkState}: HP decreases this turn while no adjacent Parasite
 *       is present (implies damage from a non-Parasite source).</li>
 * </ul>
 */
public class ScoutState implements CreatureState {

    private int lastHp = -1;

    @Override
    public void onEnter(Actor creature, GameMap map) {
        Location loc = map.locationOf(creature);
        loc.addItem(new PheromoneTrail());
        lastHp = creature.getStatistic(ActorStatistics.HEALTH);
    }

    @Override
    public CreatureState checkTransitions(Actor creature, Location location, GameMap map) {
        int currentHp = creature.getStatistic(ActorStatistics.HEALTH);

        // Check for adjacent Parasites first
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.containsAnActor() && dest.getActor().hasAbility(Ability.IS_PARASITE)) {
                lastHp = currentHp;
                return new CullingState();
            }
        }

        // If HP fell and no adjacent Parasite caused it → non-Parasite damage → Berserk
        if (lastHp != -1 && currentHp < lastHp) {
            lastHp = currentHp;
            return new BerserkState();
        }

        lastHp = currentHp;
        return this;
    }

    @Override
    public Action getAction(Actor creature, Location location) {
        Action action = new WanderBehaviour().operate(creature, location);
        return action != null ? action : new DoNothingAction();
    }

    @Override
    public String getStateName() {
        return "Scout";
    }
}
