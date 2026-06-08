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
import game.statuses.Cleansable;
import game.statuses.InfectedStatus;
import game.statuses.CleanseFieldStatus;
import game.statuses.KineticLinkStatus;
import game.statuses.ReinforcedPlatesStatus;
import game.statuses.ResurrectionProtocolStatus;
import game.statuses.ShieldStatus;
import game.display.EventLog;
import game.grounds.StasisPod;
import game.upgrades.WorkerProgression;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Ground;

import game.statuses.LockedOutStatus;
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
 * equipped with a {@link game.weapons.WorkerFist} intrinsic weapon,
 * allowing Workers to attack each other and trigger
 * {@link game.penalty.FriendlyFireTrigger}. Also checks for
 * {@link game.statuses.LockedOutStatus} each turn to enforce infraction lockouts.
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
        this.enableAbility(Ability.IS_WORKER);
        this.addNewStatistic(game.ActorStatistics.CREDITS, new BaseStatistic(MAX_CREDITS));
        this.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.UPDATE, INITIAL_CREDITS);
        this.setIntrinsicWeapon(new WorkerFist());
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
     * REQ3 — Healer T2 Cleanse Field immunity. A worker carrying a
     * {@link CleanseFieldStatus} silently drops any incoming
     * {@link Cleansable} status (Poison, Burn, future cleansables) before
     * it can attach. All other status types pass through unchanged.
     * <p>
     * Cleansability is dispatched via the {@link Cleansable} marker interface
     * rather than the concrete {@code TimedDamageStatus} class — future
     * cleansable status types just implement the marker (OCP).
     *
     * @param status the candidate status being applied
     */
    @Override
    public void addStatus(Status status) {
        if (status instanceof Cleansable && hasStatus(CleanseFieldStatus.class)) {
            return;
        }
        super.addStatus(status);
    }

    /**
     * REQ3 — Healer T3 Resurrection Protocol death intercept. A worker
     * carrying a {@link ResurrectionProtocolStatus} drops a
     * {@link StasisPod} on their current tile (capturing the original ground
     * underneath plus a reference back to themselves) before deferring to the
     * engine's normal removal. Another worker can later consume the pod to
     * revive this one — see {@link StasisPod#tick}.
     *
     * @param map the map this worker is on
     * @return a description suitable for the game log
     */
    @Override
    public String unconscious(GameMap map) {
        dropStasisPodIfEligible(map);
        return super.unconscious(map);
    }

    /**
     * Death-by-attacker variant of {@link #unconscious(GameMap)} — same
     * Resurrection Protocol behaviour.
     *
     * @param otherActor the actor that killed this one
     * @param map        the map this worker is on
     * @return a description suitable for the game log
     */
    @Override
    public String unconscious(Actor otherActor, GameMap map) {
        dropStasisPodIfEligible(map);
        return super.unconscious(otherActor, map);
    }

    /** Replaces this worker's tile with a {@link StasisPod} iff the protocol is installed. */
    private void dropStasisPodIfEligible(GameMap map) {
        if (!hasStatus(ResurrectionProtocolStatus.class)) {
            return;
        }
        Location here = map.locationOf(this);
        Ground originalGround = here.getGround();
        here.setGround(new StasisPod(this, originalGround));
        EventLog.getInstance().log(
                this + " is preserved in a Stasis Pod at ("
                        + here.x() + ", " + here.y() + "). Another worker can consume it to revive.");
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
     * Provides an {@link AttackAction} targeting this Worker when another
     * ContractedWorker is adjacent. This enables friendly fire between Workers,
     * which triggers the {@link game.penalty.FriendlyFireTrigger} infraction check
     * inside {@link AttackAction#execute(Actor, GameMap)}.
     *
     * @param otherActor the adjacent actor
     * @param direction  the direction of the adjacent actor
     * @param map        the current game map
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = super.allowableActions(otherActor, direction, map);
        if (otherActor.hasAbility(Ability.IS_WORKER)) {
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
     * if the Worker has an active {@link game.statuses.LockedOutStatus},
     * skips the menu entirely and returns a DoNothingAction for the duration of the lockout.
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

        // REQ3 — offer attack options against any adjacent hostile.
        // Every worker has a WorkerFist from construction (REQ4 friendly-fire mechanic);
        // installing the Plasma Gauntlet (Combat T3) replaces it with a stronger weapon.
        addAttackOptions(actions, map);

        // REQ3 — if the worker has a charged Phase Stabilizer, offer to phase
        // through any adjacent wall.
        addPhaseThroughWallOptions(actions, map);

        // REQ3 — surface any pending phase-warp offers staged by adjacent Warper Trees.
        addPendingWarpOptions(actions);

        // Handle multi-turn Actions
        List<LockedOutStatus> locks = this.statusesOf(LockedOutStatus.class);
        if (!locks.isEmpty()) {
            display.println(this.name + " is locked out! (" + locks.get(0) + ")");
            return new DoNothingAction();
        }

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
     * {@link Ability#HOSTILE_TO_WORKERS} actor. Every worker is equipped with
     * a {@link WorkerFist} from construction (used by REQ4's friendly-fire
     * mechanic), so this pathway is available immediately — the Combat T3
     * Plasma Gauntlet upgrade <em>replaces</em> the fist with a much stronger
     * {@link game.weapons.PlasmaGauntletWeapon}, but it is not the unlock.
     * The null-guard remains as defensive code for any future actor type that
     * deliberately ships without an intrinsic weapon.
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