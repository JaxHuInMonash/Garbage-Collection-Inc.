package game.states.swarmling;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ChainStrikeAction;
import game.actors.StatefulCreature;
import game.capabilities.Ability;
import game.states.CreatureState;
import game.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The Swarmling's rage state. Unique Behaviour 2.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Tidal Strike — all adjacent
 * actors take 15 damage and are knocked back 3 tiles away from the Swarmling.
 * <p>
 * <strong>Unique action:</strong> Chain Strike — a {@link ChainStrikeAction}
 * that attacks the primary target and then deals 50% shockwave damage to every
 * other adjacent actor.
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link CullingState}: HP drops below 20% of maximum.</li>
 *   <li>→ {@link DormantState}: 3 entities are killed while in this state.</li>
 * </ul>
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class BerserkState implements CreatureState {

    private static final int TIDAL_STRIKE_DAMAGE = 15;
    private static final int KNOCKBACK_STEPS = 3;
    private static final int KILLS_TO_CALM = 3;
    private static final double LOW_HP_THRESHOLD = 0.20;

    private int killCount = 0;

    @Override
    public void onEnter(StatefulCreature creature, GameMap map) {
        Location loc = map.locationOf(creature);
        // Snapshot exits before we start moving actors
        List<Exit> exits = new ArrayList<>(loc.getExits());
        for (Exit exit : exits) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor()) continue;
            Actor adjacent = dest.getActor();
            adjacent.hurt(TIDAL_STRIKE_DAMAGE);
            if (!adjacent.isConscious()) {
                adjacent.unconscious(creature, map);
                killCount++;
            } else {
                knockback(adjacent, loc, map, KNOCKBACK_STEPS);
            }
        }
    }

    @Override
    public CreatureState checkTransitions(StatefulCreature creature, Location location, GameMap map) {
        if (killCount >= KILLS_TO_CALM) {
            return new DormantState();
        }
        int currentHp = creature.getStatistic(ActorStatistics.HEALTH);
        int maxHp = creature.getMaximumStatistic(ActorStatistics.HEALTH);
        if (currentHp < maxHp * LOW_HP_THRESHOLD) {
            return new CullingState();
        }
        return this;
    }

    @Override
    public Action getAction(StatefulCreature creature, Location location) {
        // Prefer Chain Strike on any attackable adjacent actor
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor()) continue;
            Actor target = dest.getActor();
            if (target.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                return new ChainStrikeAction(target, () -> killCount++);
            }
        }
        return wanderOrDoNothing(creature, location);
    }

    @Override
    public String getStateName() {
        return "Berserk";
    }

    /** Pushes {@code actor} up to {@code steps} tiles away from {@code origin}. */
    private void knockback(Actor actor, Location origin, GameMap map, int steps) {
        for (int i = 0; i < steps; i++) {
            if (!map.contains(actor)) return;
            Location current = map.locationOf(actor);
            Location best = null;
            double bestDist = -1;

            for (Exit exit : current.getExits()) {
                Location dest = exit.getDestination();
                if (!dest.canActorEnter(actor)) continue;
                double dist = LocationUtils.euclidean(dest, origin);
                if (dist > bestDist) {
                    bestDist = dist;
                    best = dest;
                }
            }

            if (best == null) break;
            map.moveActor(actor, best);
        }
    }

}
