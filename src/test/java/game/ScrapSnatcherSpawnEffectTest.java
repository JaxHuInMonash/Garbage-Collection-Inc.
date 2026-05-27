package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ScrapSnatcher;
import game.actors.Slime;
import game.capabilities.Sellable;
import game.items.AlienArtifact;
import game.items.AluminiumScrap;
import game.items.IndustrialFan;
import game.spawning.ScrapSnatcherSpawnEffect;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScrapSnatcherSpawnEffect — the "loot explosion" that
 * scatters depositable resources on adjacent tiles when a Snatcher spawns.
 */
class ScrapSnatcherSpawnEffectTest {

    private static final List<Supplier<Item>> POOL = List.of(
            AluminiumScrap::new, IndustrialFan::new, AlienArtifact::new);

    @Test
    public void lootExplosionDropsOneItemOnEveryOpenAdjacentTile() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Location centre = map.at(2, 2);
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, centre);

        new ScrapSnatcherSpawnEffect(POOL).apply(snatcher, centre);

        for (Exit exit : centre.getExits()) {
            Location adj = exit.getDestination();
            assertEquals(1, adj.getItems().size());
            assertTrue(adj.getItems().get(0) instanceof Sellable);
        }
    }

    @Test
    public void lootExplosionSkipsWallsAdjacentToTheSnatcher() throws GameEngineException {
        GameMap map = TestMaps.walledRoom5x5();
        Location corner = map.at(1, 1);              // touches several walls
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, corner);

        new ScrapSnatcherSpawnEffect(POOL).apply(snatcher, corner);

        for (Exit exit : corner.getExits()) {
            Location adj = exit.getDestination();
            if (!adj.canActorEnter(snatcher)) {
                assertTrue(adj.getItems().isEmpty(),
                        "Wall tile should not receive items.");
            }
        }
    }

    @Test
    public void lootExplosionSkipsTilesOccupiedByOtherActors() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Location centre = map.at(2, 2);
        Location north  = map.at(2, 1);
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, centre);
        map.addActor(new Slime(), north);            // blocks the north tile

        new ScrapSnatcherSpawnEffect(POOL).apply(snatcher, centre);

        assertTrue(north.getItems().isEmpty());
    }

    @Test
    public void lootExplosionRespectsTheInjectedPool() throws GameEngineException {
        // With only AluminiumScrap in the pool, every dropped item must be one.
        GameMap map = TestMaps.floor5x5();
        Location centre = map.at(2, 2);
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, centre);

        new ScrapSnatcherSpawnEffect(List.of(AluminiumScrap::new))
                .apply(snatcher, centre);

        for (Exit exit : centre.getExits()) {
            Location adj = exit.getDestination();
            if (!adj.getItems().isEmpty()) {
                assertEquals(AluminiumScrap.class, adj.getItems().get(0).getClass());
            }
        }
    }
}