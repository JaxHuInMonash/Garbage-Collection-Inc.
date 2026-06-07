package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.grounds.Floor;
import game.grounds.Wall;

import java.util.Arrays;

/**
 * Tiny helpers for building game maps inside tests.
 * <p>
 * Each map is registered with a throwaway {@link World} so that
 * {@code map.addActor(...)} works — otherwise the map's internal
 * {@code actorLocations} field stays null and any actor placement NPEs.
 * @author Mary
 */
public final class TestMaps {

    private TestMaps() {}

    /** Minimal concrete World — World is abstract but has no abstract methods. */
    private static final class TestWorld extends World {
        TestWorld() { super(new Display()); }
    }

    /** 5×5 map of all floor tiles. Centre is (2, 2). */
    public static GameMap floor5x5() {
        try {
            DefaultGroundCreator creator = new DefaultGroundCreator();
            creator.registerGround('_', Floor::new);
            GameMap map = new GameMap("test", creator, Arrays.asList(
                    "_____",
                    "_____",
                    "_____",
                    "_____",
                    "_____"));
            new TestWorld().addGameMap(map);    // wires actorLocations
            return map;
        } catch (GameEngineException e) {
            throw new RuntimeException("Failed to build test map", e);
        }
    }

    /** 5×5 map: walls around the rim, floor inside. (1,1) is a corner. */
    public static GameMap walledRoom5x5() {
        try {
            DefaultGroundCreator creator = new DefaultGroundCreator();
            creator.registerGround('_', Floor::new);
            creator.registerGround('#', Wall::new);
            GameMap map = new GameMap("test", creator, Arrays.asList(
                    "#####",
                    "#___#",
                    "#___#",
                    "#___#",
                    "#####"));
            new TestWorld().addGameMap(map);    // wires actorLocations
            return map;
        } catch (GameEngineException e) {
            throw new RuntimeException("Failed to build test map", e);
        }
    }
}