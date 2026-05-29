package game.items;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.actors.Slime;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.spawning.SpawnEffectManager;

/**
 * Industrial Fan — dropped when a Vent is cut by the Plasma Cutter.
 *
 * <p>The worker can choose to:
 * <ul>
 *   <li><b>Sell</b> for 150 Worker Credits — but selling strips the facility's
 *       cooling system: a {@link Slime} instantly spawns on an empty tile
 *       adjacent to the Supercomputer.</li>
 *   <li><b>Deposit</b> for 10 Company Credits — the Company rewards compliance
 *       with a ventilation override: the worker is healed for 10 HP.</li>
 * </ul>
 *
 * <p>Both paths remove the item from the inventory (consumed on use — DRY,
 * each path calls {@code inventory.remove(this)} exactly once in its own
 * callback — no shared mutable flag needed).
 *
 * @author (your name)
 */
public class IndustrialFan extends Item implements Sellable, Depositable {

    private static final int WEIGHT        = 5;
    private static final int SELL_VALUE    = 150;
    private static final int DEPOSIT_VALUE = 10;
    private static final int HEAL_AMOUNT   = 10;

    /**
     * Constructor.
     */
    public IndustrialFan() {
        super("Industrial Fan", '@');
        this.addNewStatistic(ItemStatistics.WEIGHT,     new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    // ── Sellable ─────────────────────────────────────────────────────────────

    /**
     * Sells the fan for 150 Worker Credits. Spawns a Slime on an empty tile
     * adjacent to the Supercomputer as a hazard penalty.
     */
    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        spawnSlimeAdjacentToSupercomputer(map);
        String msg = actor + " sold the Industrial Fan for " + SELL_VALUE
                + " worker credits — a Slime spawns near the Supercomputer!";
        System.out.println(msg);
        return msg;
    }

    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }

    // ── Depositable ──────────────────────────────────────────────────────────

    /**
     * Deposits the fan for 10 Company Credits. Heals the worker for 10 HP.
     */
    @Override
    public String onDeposited(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.INCREASE, HEAL_AMOUNT);
        String msg = actor + " deposited the Industrial Fan for " + DEPOSIT_VALUE
                + " company credits. Fresh oxygen heals " + actor + " for " + HEAL_AMOUNT + " HP!";
        System.out.println(msg);
        return msg;
    }

    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Finds the Supercomputer on the map and spawns a Slime on an adjacent
     * empty tile. Scans all map tiles to locate the Supercomputer ground.
     */
    private void spawnSlimeAdjacentToSupercomputer(GameMap map) {
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location loc = map.at(x, y);
                if (loc.getGround() instanceof game.grounds.Supercomputer) {
                    trySpawnSlimeAdjacent(loc);
                    return;
                }
            }
        }
    }

    private void trySpawnSlimeAdjacent(Location supercomputerLocation) {
        for (Exit exit : supercomputerLocation.getExits()) {
            Location adjacent = exit.getDestination();
            Slime slime = new Slime();
            if (!adjacent.containsAnActor() && adjacent.canActorEnter(slime)) {
                try {
                    adjacent.addActor(slime);
                    SpawnEffectManager.getInstance().onCreatureSpawned(slime, adjacent);
                } catch (GameEngineException e) {
                    // Tile became occupied between check and placement — skip
                }
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "Industrial Fan";
    }
}