package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A capability interface for entities (Actors or Items) that can be infected
 * by a Parasite creature.
 * <p>
 * Each implementor defines its own reaction to infection via
 * {@link #onInfected(Actor, GameMap)}. Because this is an interface,
 * it works with the engine's {@code GameEntity.asCapability()} system,
 * allowing polymorphic discovery of infectable targets on the map.
 *
 * @author Jax
 */
public interface Infectable {

    /**
     * Called when this entity is infected by a Parasite.
     * Implementations should apply the appropriate infection status
     * to themselves.
     *
     * @param infector the Parasite that caused the infection
     * @param map      the current game map
     */
    void onInfected(Actor infector, GameMap map);
}
