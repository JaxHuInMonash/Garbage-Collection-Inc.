package game.actors;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Ability;
import game.capabilities.Infectable;
import game.spawning.SpawnEffectManager;
import game.statuses.InfectedStatus;

/**
 * This brave soul is capable of performing complex tasks such as picking up trash
 * off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive.
 * <p>
 * The ContractedWorker is the player-controlled actor in the game. All allowable
 * actions (item interactions, ground interactions, movement) are handled by the
 * engine's {@link edu.monash.fit2099.engine.positions.World#run()} loop, which
 * delegates to Item and Ground allowableActions methods. This class focuses
 * solely on consciousness checking and menu display.
 *
 * @author Jax
 */
public class ContractedWorker extends Actor implements Infectable {

    private static final int INFECTION_DAMAGE_PER_TURN = 1;
    private static final int INFECTION_SPAWN_INTERVAL = 5;

    private boolean infected;
    private int infectionTurnCount;

    /**
     * Constructor.
     *
     * @param name        the name of this worker
     * @param displayChar the character representing this worker on the map
     * @param hitPoints   the starting health of this worker
     * @param inventory   the inventory this worker carries
     */
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
        this.enableAbility(Ability.ATTACKABLE_BY_CREATURES);
        this.infected = false;
        this.infectionTurnCount = 0;
    }

    /**
     * Called when this worker is infected by a Parasite. Adds the generic
     * {@link InfectedStatus} which delegates per-turn logic back to this class.
     *
     * @param infector the Parasite that caused the infection
     * @param map      the current game map
     */
    @Override
    public void onInfected(Actor infector, GameMap map) {
        this.infected = true;
        this.infectionTurnCount = 0;
        this.addStatus(new InfectedStatus(this));
    }

    /**
     * Per-turn infection logic: takes 1 damage each turn. Every 5 turns,
     * attempts to spawn a Parasite on an adjacent empty tile.
     *
     * @param location the current location of this worker
     */
    @Override
    public void onInfectionTick(Location location) {
        this.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, INFECTION_DAMAGE_PER_TURN);
        infectionTurnCount++;

        if (infectionTurnCount % INFECTION_SPAWN_INTERVAL == 0) {
            spawnParasiteAdjacent(location);
        }
    }

    /**
     * The worker's infection persists indefinitely while they remain conscious.
     *
     * @return true always (infection ends only when the worker dies)
     */
    @Override
    public boolean isInfectionActive() {
        return infected;
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
                    // Tile became occupied between check and placement — skip
                }
                return;
            }
        }
    }

    /**
     * Selects and returns an action for this worker to perform on the current turn.
     * <p>
     * If the worker is unconscious, they are removed from the map.
     * Otherwise, the engine-provided actions list (already populated by the World
     * with item, ground, and movement actions) is presented as a menu for the player.
     * Multi-turn actions are handled by checking for a continuation from the last action.
     *
     * @param actions    collection of possible Actions for this Actor (populated by the engine)
     * @param lastAction the Action this Actor took last turn
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the action chosen for this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        // Display the menu and let the player choose
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}
