package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.CutAction;
import game.capabilities.Buyable;
import game.capabilities.Cuttable;
import game.statuses.BurnStatus;

/**
 * The Plasma Cutter — a tool purchased from the Supercomputer that allows the
 * worker to cut {@link Cuttable} targets (Aluminium Doors, Vents, Alien Cubes)
 * to harvest resources toward the company quota.
 *
 * <p>Purchase side-effects (SRP — handled in {@link #onBought}):
 * <ul>
 *   <li>Deals 5 immediate damage to the buyer (ejected at searing temperature).</li>
 *   <li>Applies {@link BurnStatus} for 5 turns (1 dmg/turn).</li>
 * </ul>
 *
 * <p>The Plasma Cutter is intentionally <em>not</em> {@link game.capabilities.Sellable}
 * (by design — cannot be sold back to the company).
 *
 * <p>In-inventory behaviour: each turn, {@link #allowableActions} scans the
 * owner's adjacent tiles for {@link Cuttable} ground and adds a
 * {@link CutAction} per target. Alien Cube cutting is offered by
 * {@code AlienCube} itself (it checks for the cutter in inventory).
 *
 * @author Mary
 */
public class PlasmaCutter extends Item implements Buyable {

    private static final int WEIGHT          = 7;
    private static final int BUY_COST        = 50;
    private static final int PURCHASE_DAMAGE = 5;
    private static final int BURN_DAMAGE     = 1;
    private static final int BURN_DURATION   = 5;

    /**
     * Constructor.
     */
    public PlasmaCutter() {
        super("Plasma Cutter", '>');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_COST));
        this.makePortable();
    }

    /**
     * Places the cutter into the actor's inventory (weight-limited check
     * is performed by the inventory itself).
     *
     * @param actor the prospective buyer
     * @return true if the inventory accepted the item
     */
    @Override
    public boolean attemptAcquireBy(Actor actor) {
        return actor.getInventory().add(this);
    }

    /**
     * On a successful purchase: deals 5 immediate damage and applies
     * Burn (1 dmg/turn, 5 turns) to simulate the ejection heat.
     *
     * @param actor the buyer
     * @param map   the current map
     * @return description of the purchase side effects
     */
    @Override
    public String onBought(Actor actor, GameMap map) {
        actor.hurt(PURCHASE_DAMAGE);
        actor.addStatus(new BurnStatus(BURN_DAMAGE, BURN_DURATION));
        String msg = actor + " purchased the Plasma Cutter for " + BUY_COST
                + " credits. The searing-hot tool deals " + PURCHASE_DAMAGE
                + " damage and burns for " + BURN_DURATION + " turns!";
        System.out.println(msg);
        return msg;
    }

    @Override
    public int getBuyValue() {
        return getStatistic(ItemStatistics.BUY_VALUE);
    }

    /**
     * Scans adjacent tiles for {@link Cuttable} ground and offers a
     * {@link CutAction} for each one found.
     *
     * @param owner the actor carrying this cutter
     * @param map   the current game map
     * @return list of cut actions available this turn
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        Location ownerLocation = map.locationOf(owner);

        for (Exit exit : ownerLocation.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround() instanceof Cuttable cuttable) {
                actions.add(new CutAction(cuttable, adjacent));
            }
        }

        return actions;
    }

    @Override
    public String toString() {
        return "Plasma Cutter";
    }
}