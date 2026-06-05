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
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.Ability;
import game.capabilities.Infectable;
import game.spawning.SpawnEffectManager;
import game.statuses.InfectedStatus;
import game.statuses.KineticLinkStatus;
import game.statuses.ShieldStatus;
import game.display.EventLog;
import edu.monash.fit2099.engine.capabilities.Status;
import game.statuses.LockedOutStatus;
import game.actions.AttackAction;
import game.weapons.WorkerFist;
import java.util.List;

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

    private static final int MAX_CREDITS = 1000;
    private static final int INITIAL_CREDITS = 200;
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
        this.addNewStatistic(game.ActorStatistics.CREDITS, new BaseStatistic(MAX_CREDITS));
        this.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.UPDATE, INITIAL_CREDITS);
        this.setIntrinsicWeapon(new WorkerFist());
        this.infected = false;
        this.infectionTurnCount = 0;
    }

    /**
     * Intercepts incoming damage through any active {@link ShieldStatus} and
     * {@link KineticLinkStatus} before it reaches HP.
     * <p>
     * Processing order: Shield absorbs first, then Kinetic Link splits the
     * remainder with the linked Meeple. Only un-mitigated damage reaches HP.
     *
     * @param damage the raw incoming damage
     */
    @Override
    public void hurt(int damage) {
        int remaining = damage;

        // Shield absorbs first
        List<ShieldStatus> shields = statusesOf(ShieldStatus.class);
        if (!shields.isEmpty()) {
            ShieldStatus shield = shields.get(0);
            int absorbed = shield.absorb(remaining);
            remaining -= absorbed;
            if (!shield.isStatusActive()) {
                removeStatus(shield);
            }
        }

        // Kinetic Link splits remaining damage with the Meeple
        if (remaining > 0) {
            List<KineticLinkStatus> links = statusesOf(KineticLinkStatus.class);
            if (!links.isEmpty()) {
                KineticLinkStatus link = links.get(0);
                remaining = link.share(remaining);
                if (!link.isStatusActive()) {
                    removeStatus(link);
                }
            }
        }

        if (remaining > 0) {
            super.hurt(remaining);
        }
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
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = super.allowableActions(otherActor, direction, map);
        if (otherActor instanceof ContractedWorker) {
            actions.add(new AttackAction(this));
        }
        return actions;
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

        List<LockedOutStatus> locks = this.statusesOf(LockedOutStatus.class);
        if (!locks.isEmpty()) {
            display.println(this.name + " is locked out! (" + locks.get(0) + ")");
            return new DoNothingAction();
        }

        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        displayEventLog(display);
        displayActiveStatuses(display);
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
    /**
     * Prints all queued event messages (e.g., creature spawns) that occurred
     * since the player's last turn, then clears the queue.
     *
     * @param display the display to print to
     */
    private void displayEventLog(Display display) {
        List<String> events = EventLog.getInstance().drain();
        if (!events.isEmpty()) {
            display.println("--- Events ---");
            for (String event : events) {
                display.println("  " + event);
            }
        }
    }

    /**
     * Prints the worker's currently active status effects so the player
     * knows what is affecting them before choosing an action.
     *
     * @param display the display to print to
     */
    private void displayActiveStatuses(Display display) {
        List<Status> activeStatuses = this.statuses();
        if (!activeStatuses.isEmpty()) {
            display.println("--- " + this.name + "'s Active Statuses ---");
            for (Status status : activeStatuses) {
                display.println("  * " + status);
            }
        }
    }

    /**
     * Returns a string representation including both HP and credits.
     * <p>
     * Format: {@code name - hp(current/max), credit(current/max)}
     *
     * @return the formatted display string
     */
    @Override
    public String toString() {
        int hp = this.getStatistic(ActorStatistics.HEALTH);
        int maxHp = this.getMaximumStatistic(ActorStatistics.HEALTH);
        int credits = this.getStatistic(game.ActorStatistics.CREDITS);
        int maxCredits = this.getMaximumStatistic(game.ActorStatistics.CREDITS);
        return this.name + " - hp(" + hp + "/" + maxHp + "), credit(" + credits + "/" + maxCredits + ")";
    }
}