package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Parasite;
import game.spawning.SpawnEffectManager;

/**
 * An infection status applied to workers (and Undead) by Parasites.
 * <p>
 * Each turn, the infected entity takes 1 damage. Every 5 turns, a new
 * Parasite is spawned on an adjacent empty tile (if available). The
 * infection persists indefinitely while the entity remains conscious.
 *
 * @author Jax
 */
public class WorkerInfectedStatus implements Status {

    private static final int DAMAGE_PER_TURN = 1;
    private static final int SPAWN_INTERVAL = 5;

    private int turnCount;
    private boolean active;

    /**
     * Constructor.
     */
    public WorkerInfectedStatus() {
        this.turnCount = 0;
        this.active = true;
    }

    /**
     * Each tick: deal 1 damage to the infected entity. Every 5 turns,
     * attempt to spawn a Parasite on an adjacent empty tile.
     *
     * @param currEntity the infected entity
     * @param location   the location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        currEntity.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, DAMAGE_PER_TURN);
        turnCount++;

        if (turnCount % SPAWN_INTERVAL == 0) {
            spawnParasiteAdjacent(location);
        }
    }

    @Override
    public boolean isStatusActive() {
        return active;
    }

    /**
     * Attempts to spawn a Parasite on an adjacent empty tile.
     * If no empty adjacent tile exists, the spawn is silently skipped.
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
                    // Tile became occupied between check and placement — skip
                }
                return;
            }
        }
    }
}
