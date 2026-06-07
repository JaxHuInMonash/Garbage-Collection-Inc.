package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.statuses.PoisonStatus;

import java.util.List;
import java.util.Random;

/**
 * Alien Artifact — dropped when an {@link game.items.AlienCube} is cut by the
 * Plasma Cutter (while held in inventory).
 *
 * <p>The worker can choose to:
 * <ul>
 *   <li><b>Sell</b> for 200 Worker Credits — 50 % chance of being poisoned
 *       (1 dmg/turn, 5 turns) from handling the unstable artifact.</li>
 *   <li><b>Deposit</b> for 100 Company Credits — the worker is immediately
 *       teleported to a random valid location on the same map.</li>
 * </ul>
 *
 * @author Mary
 */
public class AlienArtifact extends Item implements Sellable, Depositable {

    private static final int WEIGHT          = 1;
    private static final int SELL_VALUE      = 200;
    private static final int DEPOSIT_VALUE   = 100;
    private static final double POISON_CHANCE = 0.50;
    private static final int POISON_DAMAGE   = 1;
    private static final int POISON_DURATION = 5;

    private static final Random RANDOM = new Random();

    /** Constructor. */
    public AlienArtifact() {
        super("Alien Artifact", '?');
        this.addNewStatistic(ItemStatistics.WEIGHT,     new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    // ── Sellable ─────────────────────────────────────────────────────────────

    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        StringBuilder sb = new StringBuilder();
        sb.append(actor).append(" sold the Alien Artifact for ").append(SELL_VALUE)
                .append(" worker credits.");

        if (RANDOM.nextDouble() < POISON_CHANCE) {
            actor.addStatus(new PoisonStatus(POISON_DAMAGE, POISON_DURATION));
            sb.append(" The unstable artifact poisons ").append(actor).append("!");
        }

        String msg = sb.toString();
        System.out.println(msg);
        return msg;
    }

    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }

    // ── Depositable ──────────────────────────────────────────────────────────

    /**
     * Deposits the artifact for 100 Company Credits and teleports the worker
     * to a random valid (passable, unoccupied) location on the same map.
     */
    @Override
    public String onDeposited(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        List<edu.monash.fit2099.engine.positions.Location> candidates = new java.util.ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                edu.monash.fit2099.engine.positions.Location loc = map.at(x, y);
                if (!loc.containsAnActor() && loc.canActorEnter(actor)) {
                    candidates.add(loc);
                }
            }
        }

        String msg;
        if (!candidates.isEmpty()) {
            edu.monash.fit2099.engine.positions.Location dest =
                    candidates.get(RANDOM.nextInt(candidates.size()));
            map.moveActor(actor, dest);
            msg = actor + " deposited the Alien Artifact for " + DEPOSIT_VALUE
                    + " company credits. " + actor + " is teleported to ("
                    + dest.x() + ", " + dest.y() + ")!";
        } else {
            msg = actor + " deposited the Alien Artifact for " + DEPOSIT_VALUE
                    + " company credits. No valid teleport destination found.";
        }

        System.out.println(msg);
        return msg;
    }

    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    @Override
    public String toString() {
        return "Alien Artifact";
    }
}