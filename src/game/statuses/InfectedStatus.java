package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Infectable;

/**
 * A generic infection status that delegates all per-turn logic to the
 * {@link Infectable} host entity.
 * <p>
 * Rather than creating separate status classes for each host type
 * (worker, lantern, cookies, etc.), this single status class delegates
 * to the host's {@link Infectable#onInfectionTick(Location)} and
 * {@link Infectable#isInfectionActive()} methods. This keeps infection
 * behaviour encapsulated within each host, where it belongs.
 *
 * @author Jax
 */
public class InfectedStatus implements Status {

    private final Infectable host;

    /**
     * Constructor.
     *
     * @param host the infectable entity this status is applied to
     */
    public InfectedStatus(Infectable host) {
        this.host = host;
    }

    /**
     * Delegates the per-turn infection tick to the host entity.
     * Each host defines its own behaviour (damage, resource drain,
     * creature spawning, etc.).
     *
     * @param currEntity the entity this status is attached to
     * @param location   the current location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        host.onInfectionTick(location);
    }

    /**
     * Delegates the active check to the host entity. When the host
     * reports the infection is no longer active, this status will be
     * removed by the engine.
     *
     * @return true if the host's infection is still active
     */
    @Override
    public boolean isStatusActive() {
        return host.isInfectionActive();
    }
}


