package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A functional interface representing an environmental reaction that occurs
 * when a creature is spawned into the game world.
 * <p>
 * Each creature type (Undead, Slime, Parasite) has a distinct spawning effect
 * that triggers regardless of what spawner created it. Implementations are
 * registered with the {@link SpawnEffectManager} and dispatched by creature type.
 *
 * @author Jax
 */
@FunctionalInterface
public interface SpawnEffect {

    /**
     * Applies the environmental reaction caused by a creature spawning.
     *
     * @param creature the newly spawned creature
     * @param location the location where the creature was placed
     */
    void apply(Actor creature, Location location);
}
