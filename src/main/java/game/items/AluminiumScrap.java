package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Depositable;
import game.grounds.IronDoor;

import java.util.Random;

/**
 * Aluminium Scrap — dropped when an {@link IronDoor.AluminiumDoor} is cut.
 *
 * <p>Can only be <em>deposited</em> at the Supercomputer (not sold); depositing
 * yields 50 Company Credits. There is a 20 % chance the worker is cut for
 * 5 damage while shoving the jagged metal into the deposit chute.
 *
 * <p>Design notes:
 * <ul>
 *   <li>Implements only {@link Depositable} (not {@link game.capabilities.Sellable})
 *       — faithful to the spec and avoids dead methods (design smell: Lazy Class).</li>
 *   <li>All magic numbers are private constants (DRY).</li>
 *   <li>Side-effect logic lives entirely in {@link #onDeposited} (SRP).</li>
 * </ul>
 *
 * @author Mary
 */
public class AluminiumScrap extends Item implements Depositable {

    private static final int WEIGHT           = 2;
    private static final int DEPOSIT_VALUE    = 50;
    private static final int CUT_DAMAGE       = 5;
    private static final double CUT_CHANCE    = 0.20;

    private static final Random RANDOM = new Random();

    /** Constructor. */
    public AluminiumScrap() {
        super("Aluminium Scrap", '%');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    // ── Depositable ──────────────────────────────────────────────────────────

    @Override
    public String onDeposited(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        StringBuilder sb = new StringBuilder();
        sb.append(actor).append(" deposited Aluminium Scrap for ").append(DEPOSIT_VALUE)
                .append(" company credits.");

        if (RANDOM.nextDouble() < CUT_CHANCE) {
            actor.hurt(CUT_DAMAGE);
            sb.append(" The jagged metal cuts ").append(actor).append(", dealing ")
                    .append(CUT_DAMAGE).append(" damage!");
        }

        String msg = sb.toString();
        System.out.println(msg);
        return msg;
    }

    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    @Override
    public String toString() {
        return "Aluminium Scrap";
    }
}