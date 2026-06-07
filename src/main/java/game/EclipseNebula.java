package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.*;
import game.alarm.AlarmManager;
import game.items.ScrapPile;
import game.grounds.*;
import game.items.*;
import game.spawning.ParasiteSpawnEffect;
import game.spawning.SlimeSpawnEffect;
import game.spawning.SpawnEffectManager;
import game.spawning.UndeadSpawnEffect;
import game.display.EventLog;
import game.upgrades.CompanyAccount;
import game.upgrades.InMemoryCompanyAccount;
import game.upgrades.fighter.FighterStation;
import game.upgrades.healer.HealerStation;
import game.upgrades.surveyor.SurveyorStation;

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
        // Reset singletons for a fresh game
        AlarmManager.resetInstance();
        SpawnEffectManager.resetInstance();

        // Register spawn effects for each creature type
        SpawnEffectManager.getInstance().registerEffect(Undead.class, new UndeadSpawnEffect());
        SpawnEffectManager.getInstance().registerEffect(Slime.class, new SlimeSpawnEffect());
        SpawnEffectManager.getInstance().registerEffect(Parasite.class, new ParasiteSpawnEffect());

        // Single ground creator shared across all maps
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', AluminiumDoor::new);
        groundCreator.registerGround('N', IronDoor::new);
        groundCreator.registerGround('M', TitaniumDoor::new);
        groundCreator.registerGround('o', () -> new Hole(List.of(Undead::new, Slime::new)));
        groundCreator.registerGround('O', () -> new Hole(List.of(Undead::new, Parasite::new)));
        groundCreator.registerGround('V', Vent::new);
        groundCreator.registerGround('≡', Supercomputer::new);
        groundCreator.registerGround('a', AlarmFloor::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
        groundCreator.registerGround('◎', MagicCircle::new);
        groundCreator.registerGround('Φ', Floor::new);
        groundCreator.registerGround('◈', Floor::new);

        // Flora ground types (used by 20-Overflow map)
        groundCreator.registerGround('y', FleshyTreeSprout::new);
        groundCreator.registerGround('v', FleshyTreeSapling::new);
        groundCreator.registerGround('Y', FleshyTreeMature::new);
        groundCreator.registerGround('w', WarperTreeSapling::new);
        groundCreator.registerGround('W', WarperTreeMature::new);

        // 99-Deprecated
        List<String> moon99Deprecated = Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#Φ____=...~......#__________________#___________________#",
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

        // 20-Overflow
        List<String> moon20Overflow = Arrays.asList(
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#######...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#________________#≈≈≈≈≈≈≈",
                "...#__Φ__=...........≈≈≈≈≈≈≈≈#######_______◈________#≈≈≈≈≈≈≈",
                "...#_____#...........≈≈≈≈≈≈≈≈#_____N________________#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______O______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______O______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______M__________◈__≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        );

        GameMap moon20OverflowMap = new GameMap("20-Overflow", groundCreator, moon20Overflow);
        this.addGameMap(moon20OverflowMap);

        // Set up TeleportationTubes — each tube must support both within-map
        // and between-maps movement.
        TeleportationTube tube99 = new TeleportationTube();
        tube99.addDestination(moon20OverflowMap, 25, 10, "20-Overflow (drop zone)");
        tube99.addDestination(moon99DeprecatedMap, 50, 14, "99-Deprecated (south corridor)"); // within-map

        TeleportationTube tube20 = new TeleportationTube();
        tube20.addDestination(moon99DeprecatedMap, 25, 1, "99-Deprecated (ship dock)");
        tube20.addDestination(moon20OverflowMap, 25, 8, "20-Overflow (east chamber)"); // within-map

        moon99DeprecatedMap.at(4, 3).setGround(tube99);
        moon20OverflowMap.at(5, 3).setGround(tube20);

        // Place the three unique ship items (only instantiated once)
        moon99DeprecatedMap.at(5, 2).addItem(new AccessCardLevel1());
        moon99DeprecatedMap.at(31, 10).addItem(new AccessCardLevel2());
        moon99DeprecatedMap.at(51, 10).addItem(new AccessCardLevel3());
        moon99DeprecatedMap.at(6, 2).addItem(new FirstAidKit());
        moon99DeprecatedMap.at(7, 2).addItem(new SterilisationBox());

        // Place scrap items throughout the moon's facility
        moon99DeprecatedMap.at(56, 1).addItem(new Apple());
        moon99DeprecatedMap.at(49, 11).addItem(new Cookies());
        moon99DeprecatedMap.at(33, 17).addItem(new Lantern());
        moon99DeprecatedMap.at(57, 17).addItem(new FloppyDisk());
        moon99DeprecatedMap.at(25, 7).addItem(new CRTMonitor());

        // Place items on the 20-Overflow map
        moon20OverflowMap.at(5, 2).addItem(new Apple());
        moon20OverflowMap.at(15, 3).addItem(new AlienCube());
        moon20OverflowMap.at(11, 15).addItem(new AlienCube());
        moon20OverflowMap.at(20, 8).addItem(new AccessCardLevel2());
        moon20OverflowMap.at(40, 14).addItem(new AccessCardLevel3());

        // Scrap piles — trigger Meeple's NomadState → EngineerState transition
        moon99DeprecatedMap.at(30, 3).addItem(new ScrapPile());
        moon99DeprecatedMap.at(50, 3).addItem(new ScrapPile());
        moon20OverflowMap.at(30, 14).addItem(new ScrapPile());

        // Swarmlings — thrive in toxic zones; place one on each map
        moon99DeprecatedMap.addActor(new Swarmling(), moon99DeprecatedMap.at(34, 6));
        moon20OverflowMap.addActor(new Swarmling(), moon20OverflowMap.at(18, 9));

        // Meeples — support units, one per map
        moon99DeprecatedMap.addActor(new Meeple(), moon99DeprecatedMap.at(44, 11));
        moon20OverflowMap.addActor(new Meeple(), moon20OverflowMap.at(22, 9));

        // REQ3 — Lab Stations placed along the east wall of the 99-Deprecated ship.
        // Workers stand on the adjacent floor tile (col 7) to interact with each station.
        // All three share a single CompanyAccount; once REQ1's quota system lands,
        // swap InMemoryCompanyAccount for the real QuotaManager (which will also
        // implement CompanyAccount).
        CompanyAccount companyAccount = new InMemoryCompanyAccount(500);
        moon99DeprecatedMap.at(12, 1).setGround(new FighterStation(companyAccount));
        moon99DeprecatedMap.at(12, 3).setGround(new SurveyorStation(companyAccount));
        moon99DeprecatedMap.at(12, 5).setGround(new HealerStation(companyAccount));

        // Register alarm consequence: spawn Security Drones at predefined locations
        registerDroneSpawnListener(moon99DeprecatedMap);

        // Create workers, each with their own WeightLimitedInventory(50) and a Flask
//        ContractedWorker contractedWorker1 = createWorker("#1 Bob");
//        ContractedWorker contractedWorker2 = createWorker("#2 Tom");
        ContractedWorker contractedWorker3 = createWorker("#3 Sarah");
//        ContractedWorker contractedWorker4 = createWorker("#4 Julie");
//        ContractedWorker contractedWorker5 = createWorker("#5 Rick");

//        this.addPlayer(contractedWorker1, moon20OverflowMap.at(14, 3));
//        this.addPlayer(contractedWorker2, moon20OverflowMap.at(5, 3));
        this.addPlayer(contractedWorker3, moon99DeprecatedMap.at(8, 3));
//        this.addPlayer(contractedWorker4, moon99DeprecatedMap.at(7, 3));
//        this.addPlayer(contractedWorker5, moon99DeprecatedMap.at(30, 10));
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
                        SecurityDrone drone = new SecurityDrone(triggeringWorker);
                        spawnPoint.addActor(drone);
                        EventLog.getInstance().log(
                                drone + " spawned at (" + spawnPoint.x() + ", "
                                        + spawnPoint.y() + ") of " + spawnPoint.map());
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
        return new ContractedWorker(name, 'ඞ', 20, inventory);
    }
}
