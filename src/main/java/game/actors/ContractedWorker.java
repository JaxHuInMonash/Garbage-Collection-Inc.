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
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.AttackAction;
import game.capabilities.Ability;
import game.capabilities.Infectable;
import game.capabilities.IntrinsicWeaponEquippable;
import game.capabilities.Progressable;
import game.spawning.SpawnEffectManager;
import game.statuses.InfectedStatus;
import game.statuses.KineticLinkStatus;
import game.statuses.ReinforcedPlatesStatus;
import game.statuses.ShieldStatus;
import game.display.EventLog;
import game.upgrades.WorkerProgression;
import edu.monash.fit2099.engine.capabilities.Status;

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
public class ContractedWorker extends Actor implements Infectable, Progressable, IntrinsicWeaponEquippable {

    private static final int MAX_CREDITS = 1000;
    private static final int INITIAL_CREDITS = 200;
    private static final int INFECTION_DAMAGE_PER_TURN = 1;
    private static final int INFECTION_SPAWN_INTERVAL = 5;

    private boolean infected;
    private int infectionTurnCount;

    /**
     * REQ3 — worker progression state (path lock, current tier, installed upgrades).
     * The Lab Stations discover this via {@link #asCapability(Class)}; the per-turn
     * passive of every installed {@link game.upgrades.Upgrade} is driven from
     * {@link #playTurn(ActionList, Action, GameMap, Display)}.
     */
    private final WorkerProgression progression = new WorkerProgression();

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
        this.infected = false;
        this.infectionTurnCount = 0;
    }

    /**
     * Exposes this worker's upgrade-progression state to {@link game.upgrades.UpgradeStation}s
     * (and tests). Implementation of the {@link Progressable} capability — looked up
     * by stations via {@code actor.asCapability(Progressable.class)}.
     */
    @Override
    public WorkerProgression getProgression() {
        return progression;
    }

    /**
     * Forwards to the engine's protected {@code setIntrinsicWeapon}. This is the
     * only legitimate path for an external component (e.g. the Plasma Gauntlet
     * upgrade) to install a weapon on a worker — discovery is via
     * {@link #asCapability(Class)} on {@link IntrinsicWeaponEquippable}.
     */
    @Override
    public void equipIntrinsicWeapon(IntrinsicWeapon weapon) {
        setIntrinsicWeapon(weapon);
    }

    /**
     * Intercepts incoming damage through the layered damage pipeline before it
     * reaches HP. Order: <strong>Shield → Reinforced Plates → Kinetic Link → HP</strong>.
     * <p>
     * Shield is a flat absorption pool (Meeple GuardianState), Reinforced Plates
     * is a percentage-based per-hit reduction with a periodic AoE discharge
     * (Combat T2 upgrade), Kinetic Link splits residual damage with the bonded
     * Meeple. Post-hit feedback ({@link game.statuses.PostHitFeedback}: reflection,
     * retaliation, …) is applied <em>after</em> the weapon resolves and is
     * driven from {@link game.actions.AttackAction}, not from here.
     *
     * @param damage the raw incoming damage
     */
    @Override
    public void hurt(int damage) {
        int remaining = damage;

        // 1. Shield absorbs first
        List<ShieldStatus> shields = statusesOf(ShieldStatus.class);
        if (!shields.isEmpty()) {
            ShieldStatus shield = shields.get(0);
            int absorbed = shield.absorb(remaining);
            remaining -= absorbed;
            if (!shield.isStatusActive()) {
                removeStatus(shield);
            }
        }

        // 2. Reinforced Plates reduce a percentage and tick toward AoE discharge
        if (remaining > 0) {
            List<ReinforcedPlatesStatus> plates = statusesOf(ReinforcedPlatesStatus.class);
            if (!plates.isEmpty()) {
                int absorbed = plates.get(0).absorb(remaining);
                remaining -= absorbed;
            }
        }

        // 3. Kinetic Link splits remaining damage with the Meeple
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

        // REQ3 — drive per-turn passives of every installed upgrade.
        progression.tick(this, map.locationOf(this));

        // REQ3 — if the worker is armed (e.g. Plasma Gauntlet installed), offer
        // attack options against any adjacent hostile.
        addAttackOptions(actions, map);

        // REQ3 — if the worker has a charged Phase Stabilizer, offer to phase
        // through any adjacent wall.
        addPhaseThroughWallOptions(actions, map);

        // REQ3 — surface any pending phase-warp offers staged by adjacent Warper Trees.
        addPendingWarpOptions(actions);

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        // Display event log (spawns, etc.) that occurred since last turn
        displayEventLog(display);

        // Display active statuses on this worker
        displayActiveStatuses(display);

        // Display the menu and let the player choose
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /**
     * Adds an {@link AttackAction} to the menu for every adjacent
     * {@link Ability#HOSTILE_TO_WORKERS} actor — but only when the worker is
     * actually armed (has an intrinsic weapon). A bare-handed worker simply
     * cannot punch back, which matches the game's pre-A3 baseline; installing
     * the Combat T3 Plasma Gauntlet upgrade is what enables this pathway.
     *
     * @param actions the action list to extend (in-place)
     * @param map     the current game map
     */
    private void addAttackOptions(ActionList actions, GameMap map) {
        if (this.getIntrinsicWeapon() == null) {
            return;
        }
        Location here = map.locationOf(this);
        for (Exit exit : here.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()
                    && adjacent.getActor().hasAbility(Ability.HOSTILE_TO_WORKERS)) {
                actions.add(new AttackAction(adjacent.getActor()));
            }
        }
    }

    /**
     * Adds a {@link game.actions.PhaseThroughWallAction} to the menu for every
     * adjacent {@link Ability#IS_WALL IS_WALL} tile — but only when the worker
     * has a charged {@link game.statuses.PhaseStabilizerStatus} (Surveyor T3).
     * The action permanently converts the wall to floor and moves the worker
     * through.
     *
     * @param actions the action list to extend (in-place)
     * @param map     the current game map
     */
    private void addPhaseThroughWallOptions(ActionList actions, GameMap map) {
        List<game.statuses.PhaseStabilizerStatus> statuses =
                this.statusesOf(game.statuses.PhaseStabilizerStatus.class);
        if (statuses.isEmpty() || !statuses.get(0).hasCharge()) {
            return;
        }
        Location here = map.locationOf(this);
        for (Exit exit : here.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround().hasAbility(Ability.IS_WALL)) {
                actions.add(new game.actions.PhaseThroughWallAction(adjacent, exit.getName()));
            }
        }
    }

    /**
     * Surfaces an {@link game.actions.AcceptWarpAction} for every
     * {@link game.statuses.PendingWarpStatus} currently staged on this worker
     * (Surveyor T3 preview-then-confirm warp from {@link game.grounds.WarperTreeMature}).
     * Each pending warp shows its destination coordinates in the menu so the
     * player can decide before committing.
     *
     * @param actions the action list to extend (in-place)
     */
    private void addPendingWarpOptions(ActionList actions) {
        for (game.statuses.PendingWarpStatus warp : this.statusesOf(game.statuses.PendingWarpStatus.class)) {
            actions.add(new game.actions.AcceptWarpAction(warp));
        }
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