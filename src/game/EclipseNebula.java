package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.actors.SecurityDrone;
import game.alarm.AlarmManager;
import game.grounds.*;
import game.items.*;

import java.util.Arrays;
import java.util.List;

/**
 * This class handles the miracle of creation, translating a bunch of periods
 * and hashtags into a sprawling, functional sci-fi facility.
 * <p>
 * It initialises the game world with maps, actors, and items.
 * Also registers alarm system listeners for REQ4.
 *
 * @author Jax
 */
public class EclipseNebula extends World {

    public EclipseNebula(Display display) {
        super(display);
    }

    /**
     * Initialise maps, actors, items, grounds, and the alarm system.
     * <p>
     * Each worker gets their own {@link WeightLimitedInventory} with a 50-unit
     * weight limit and a {@link game.items.Flask} already inside. The three special items
     * (AccessCard, FirstAidKit, SterilisationBox) are placed once in the ship.
     * <p>
     * The alarm system is initialised with a drone-spawning listener that
     * deploys Security Drones at predefined locations when triggered.
     *
     * @throws Exception in case if anything goes wrong
     */
    public void initialise() throws Exception {
        // Reset alarm state for a fresh game
        AlarmManager.resetInstance();

        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new);
        groundCreator.registerGround('o', Hole::new);
        groundCreator.registerGround('a', AlarmFloor::new);

        List<String> moon99Deprecated = Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#_____=...~......#__________________#___________________#",
                "...#_____#..~~~.....########=#####=#####___#############___#",
                "...#######.~~~~.....#______#_#_________#___#___________#___#",
                ".........~~~~.......#______#_#_________#####___________#####",
                "....................#______=_#_________#________o__________#",
                "......~.............#______#_#_________#___________________#",
                ".....~~~............#______#_###########___#############___#",
                ".....~..............#______#_____a_____#___#___________#___#",
                "....................=______#___________=___=___________=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "........~~~~~~......#______#_____a_____=___________________#",
                ".........~~~~.......#______#___________#___________________#",
                "....................#______#############___#############___#",
                "....................#______#___________#___#_____o_____#___#",
                "..~.................#______=___________=___=___________=___#",
                "....................########################################"
        );

        GameMap moon99DeprecatedMap = new GameMap("99-Deprecated", groundCreator, moon99Deprecated);
        this.addGameMap(moon99DeprecatedMap);

        // Place the three unique ship items (only instantiated once)
        moon99DeprecatedMap.at(5, 2).addItem(new AccessCard());
        moon99DeprecatedMap.at(31, 10).addItem(new AccessCard());
        moon99DeprecatedMap.at(51, 10).addItem(new AccessCard());
        moon99DeprecatedMap.at(6, 2).addItem(new FirstAidKit());
        moon99DeprecatedMap.at(7, 2).addItem(new SterilisationBox());

        // Place scrap items throughout the moon's facility
        moon99DeprecatedMap.at(56, 1).addItem(new Apple());
        moon99DeprecatedMap.at(49, 11).addItem(new Cookies());
        moon99DeprecatedMap.at(33, 17).addItem(new Lantern());
        moon99DeprecatedMap.at(57, 17).addItem(new FloppyDisk());
        moon99DeprecatedMap.at(25, 7).addItem(new CRTMonitor());

        // Register alarm consequence: spawn Security Drones at predefined locations
        registerDroneSpawnListener(moon99DeprecatedMap);

        // Create workers, each with their own WeightLimitedInventory(50) and a Flask
        ContractedWorker contractedWorker1 = createWorker("#1 Bob");
        ContractedWorker contractedWorker2 = createWorker("#2 Tom");
//        ContractedWorker contractedWorker3 = createWorker("#3 Sarah");
//        ContractedWorker contractedWorker4 = createWorker("#4 Julie");
        ContractedWorker contractedWorker5 = createWorker("#5 Rick");

        this.addPlayer(contractedWorker1, moon99DeprecatedMap.at(4, 2));
        this.addPlayer(contractedWorker2, moon99DeprecatedMap.at(50, 10));
//        this.addPlayer(contractedWorker3, moon99DeprecatedMap.at(6, 3));
//        this.addPlayer(contractedWorker4, moon99DeprecatedMap.at(7, 3));
        this.addPlayer(contractedWorker5, moon99DeprecatedMap.at(30, 10));
    }

    /**
     * Registers an alarm listener that spawns Security Drones at predefined
     * locations when the facility alarm is triggered.
     * <p>
     * The listener is implemented as a lambda, keeping the drone-spawning
     * consequence self-contained. The drone spawn locations are chosen to
     * cover key areas of the facility.
     *
     * @param map the game map to spawn drones on
     */
    private void registerDroneSpawnListener(GameMap map) {
        // Predefined drone spawn points (open floor areas in the facility)
        Location[] droneSpawnPoints = {
                map.at(30, 1),
                map.at(50, 8),
                map.at(30, 15),
        };

        AlarmManager.getInstance().registerListener((triggeringWorker, gameMap) -> {
            for (Location spawnPoint : droneSpawnPoints) {
                if (!spawnPoint.containsAnActor()) {
                    try {
                        spawnPoint.addActor(new SecurityDrone(triggeringWorker));
                    } catch (GameEngineException e) {
                        // Spawn point occupied; skip this drone
                    }
                }
            }
        });
    }

    /**
     * Factory method to create a ContractedWorker with their own inventory and flask.
     *
     * @param name the name of the worker
     * @return a new ContractedWorker with a WeightLimitedInventory(50) containing a Flask
     */
    private ContractedWorker createWorker(String name) {
        WeightLimitedInventory inventory = new WeightLimitedInventory(50);
        inventory.add(new Flask());
        return new ContractedWorker(name, 'ඞ', 10, inventory);
    }
}
