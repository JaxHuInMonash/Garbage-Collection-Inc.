package game.states.swarmling;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.capabilities.Ability;
import game.states.CreatureState;
import game.statuses.ReflectionStatus;

/**
 * The Swarmling's resting state.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Wraps the Swarmling in a
 * {@link ReflectionStatus} that absorbs the next 20 points of incoming damage.
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link ScoutState}: no Parasites within 5 tiles for 3 consecutive turns.</li>
 *   <li>→ {@link CullingState}: more than 3 Parasites enter the 3-tile radius.</li>
 * </ul>
 */
public class DormantState implements CreatureState {

    private static final int REFLECTION_AMOUNT = 20;
    private static final int SCOUT_TILE_RADIUS = 5;
    private static final int SCOUT_TURNS_REQUIRED = 3;
    private static final int CULLING_TILE_RADIUS = 3;
    private static final int CULLING_PARASITE_THRESHOLD = 3;

    private int turnsWithoutNearbyParasites = 0;

    @Override
    public void onEnter(Actor creature, GameMap map) {
        creature.addStatus(new ReflectionStatus(REFLECTION_AMOUNT));
    }

    @Override
    public void onExit(Actor creature, GameMap map) {
        // Remove any remaining reflection shield so it does not carry into new state
        creature.statusesOf(ReflectionStatus.class).forEach(creature::removeStatus);
    }

    @Override
    public CreatureState checkTransitions(Actor creature, Location location, GameMap map) {
        int parasitesIn5 = countParasitesInRadius(location, SCOUT_TILE_RADIUS);
        if (parasitesIn5 == 0) {
            turnsWithoutNearbyParasites++;
        } else {
            turnsWithoutNearbyParasites = 0;
        }

        if (turnsWithoutNearbyParasites >= SCOUT_TURNS_REQUIRED) {
            return new ScoutState();
        }

        if (countParasitesInRadius(location, CULLING_TILE_RADIUS) > CULLING_PARASITE_THRESHOLD) {
            return new CullingState();
        }

        return this;
    }

    @Override
    public Action getAction(Actor creature, Location location) {
        Action action = new WanderBehaviour().operate(creature, location);
        return action != null ? action : new DoNothingAction();
    }

    @Override
    public String getStateName() {
        return "Dormant";
    }

    private int countParasitesInRadius(Location location, int radius) {
        int count = 0;
        for (Location nearby : location.getNearbyLocations(radius)) {
            if (nearby.containsAnActor() && nearby.getActor().hasAbility(Ability.IS_PARASITE)) {
                count++;
            }
        }
        return count;
    }
}
