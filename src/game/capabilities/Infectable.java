package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A capability interface for entities (Actors or Items) that can be infected
 * by a Parasite creature.
 * <p>
 * Each implementor defines its own reaction to infection via
 * {@link #onInfected(Actor, GameMap)}, its per-turn infection behaviour via
 * {@link #onInfectionTick(Location)}, and when the infection ends via
 * {@link #isInfectionActive()}.
 * <p>
 * Because this is an interface, it works with the engine's
 * {@code GameEntity.asCapability()} system, allowing polymorphic discovery
 * of infectable targets on the map.
 *
 * @author Jax
 */
public interface Infectable {

    /**
     * Called once when this entity is first infected by a Parasite.
     * Implementations should add an {@link game.statuses.InfectedStatus}
     * to themselves and perform any immediate infection setup.
     *
     * @param infector the Parasite that caused the infection
     * @param map      the current game map
     */
    void onInfected(Actor infector, GameMap map);

    /**
     * Called each turn while the infection is active. Each host defines
     * its own per-turn infection behaviour (e.g., taking damage, draining
     * resources, spawning creatures).
     *
     * @param location the current location of this entity
     */
    void onInfectionTick(Location location);

    /**
     * Returns whether this entity's infection is still active. When this
     * returns false, the {@link game.statuses.InfectedStatus} will
     * deactivate and be removed by the engine.
     *
     * @return true if the infection is still active, false otherwise
     */
    boolean isInfectionActive();
}
