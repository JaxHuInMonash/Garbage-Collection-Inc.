package game.states.meeple;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.capabilities.Ability;
import game.capabilities.Transmutable;
import game.states.CreatureState;

/**
 * The Meeple's default wandering state.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Supply Beacon — deals 2 damage
 * to all Parasites within 10 tiles (disrupting their search) and heals all
 * Workers within 10 tiles for 2 HP (coordinates friendly activity).
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link EngineerState}: any adjacent or current tile holds a
 *       {@link Transmutable} item (e.g. UndeadCorpse) that can be converted.</li>
 *   <li>→ {@link GuardianState}: any Worker within 3 tiles is below 50% HP.</li>
 * </ul>
 */
public class NomadState implements CreatureState {

    private static final int BEACON_RADIUS = 10;
    private static final int BEACON_PARASITE_DAMAGE = 2;
    private static final int BEACON_WORKER_HEAL = 2;
    private static final int GUARDIAN_RADIUS = 3;

    @Override
    public void onEnter(StatefulCreature creature, GameMap map) {
        Location loc = map.locationOf(creature);
        for (Location nearby : loc.getNearbyLocations(BEACON_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            Actor actor = nearby.getActor();
            // Hurt Parasites
            if (actor.hasAbility(Ability.IS_PARASITE)) {
                actor.hurt(BEACON_PARASITE_DAMAGE);
                if (!actor.isConscious()) actor.unconscious(creature, map);
            }
            // Heal Workers
            if (actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                actor.heal(BEACON_WORKER_HEAL);
            }
        }
    }

    @Override
    public CreatureState checkTransitions(StatefulCreature creature, Location location, GameMap map) {
        // → Engineer if any adjacent or current tile has Transmutable items.
        // Uses Transmutable (not Scrap) to avoid a flicker loop: ScrapPile is
        // Scrap but not Transmutable, so entering EngineerState for it would
        // cause an immediate exit back to NomadState with nothing to convert.
        if (!location.getItemsAs(Transmutable.class).isEmpty()) {
            return new EngineerState();
        }
        for (Exit exit : location.getExits()) {
            if (!exit.getDestination().getItemsAs(Transmutable.class).isEmpty()) {
                return new EngineerState();
            }
        }

        // → Guardian if a nearby Worker is below 50% HP
        for (Location nearby : location.getNearbyLocations(GUARDIAN_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            Actor actor = nearby.getActor();
            if (actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                int hp = actor.getStatistic(ActorStatistics.HEALTH);
                int maxHp = actor.getMaximumStatistic(ActorStatistics.HEALTH);
                if (hp < maxHp / 2) {
                    return new GuardianState();
                }
            }
        }

        return this;
    }

    @Override
    public Action getAction(StatefulCreature creature, Location location) {
        return wanderOrDoNothing(creature, location);
    }

    @Override
    public String getStateName() {
        return "Nomad";
    }
}
