package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.items.Cookies;
import game.spawning.SpawnEffectManager;

/**
 * An infection status applied to a {@link Cookies} item by a Parasite.
 * <p>
 * Each turn, the remaining cookie count is reduced by 1. Additionally,
 * each turn a Parasite is spawned on an adjacent empty tile (if available).
 * The infection remains active while cookies remain.
 *
 * @author Jax
 */
public class CookieInfectedStatus implements Status {

    private final Cookies cookies;

    /**
     * Constructor.
     *
     * @param cookies the cookies item being infected
     */
    public CookieInfectedStatus(Cookies cookies) {
        this.cookies = cookies;
    }

    /**
     * Each tick: reduce cookies by 1 and attempt to spawn a Parasite
     * on an adjacent empty tile.
     *
     * @param currEntity the infected cookies item
     * @param location   the location of the cookies
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        cookies.reduceCookies(1);
        spawnParasiteAdjacent(location);
    }

    /**
     * The infection remains active while cookies remain.
     *
     * @return true if there are cookies remaining
     */
    @Override
    public boolean isStatusActive() {
        return cookies.getRemainingCookies() > 0;
    }

    /**
     * Attempts to spawn a Parasite on an adjacent empty tile.
     *
     * @param location the location around which to spawn
     */
    private void spawnParasiteAdjacent(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Parasite parasite = new Parasite();
            if (adjacent.canActorEnter(parasite)) {
                try {
                    adjacent.addActor(parasite);
                    SpawnEffectManager.getInstance().onCreatureSpawned(parasite, adjacent);
                } catch (GameEngineException e) {
                    // Tile became occupied — skip
                }
                return;
            }
        }
    }
}
