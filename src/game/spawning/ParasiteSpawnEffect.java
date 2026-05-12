package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;

/**
 * Spawn effect for Parasite: all workers within its adjacent tiles
 * immediately take 2 points of damage.
 *
 * @author Jax
 */
public class ParasiteSpawnEffect implements SpawnEffect {

    private static final int SPAWN_DAMAGE = 2;

    @Override
    public void apply(Actor creature, Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                Actor actor = adjacent.getActor();
                if (actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                    actor.hurt(SPAWN_DAMAGE);
                }
            }
        }
    }
}
