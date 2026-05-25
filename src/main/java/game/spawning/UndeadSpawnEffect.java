package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Spawn effect for Undead: for every creature currently standing in its
 * adjacent tiles at the moment it spawns, the Undead's maximum health
 * is permanently increased by 1.
 *
 * @author Jax
 */
public class UndeadSpawnEffect implements SpawnEffect {

    @Override
    public void apply(Actor creature, Location location) {
        int adjacentCreatures = 0;
        for (Exit exit : location.getExits()) {
            if (exit.getDestination().containsAnActor()) {
                adjacentCreatures++;
            }
        }
        if (adjacentCreatures > 0) {
            creature.modifyStatisticMaximum(
                    ActorStatistics.HEALTH, StatisticOperations.INCREASE, adjacentCreatures);
        }
    }
}
