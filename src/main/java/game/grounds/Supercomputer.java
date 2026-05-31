package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ActorStatistics;
import game.actions.BuyAction;
import game.actions.DepositAction;
import game.actions.SellAction;
import game.capabilities.Ability;
import game.capabilities.Buyable;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.display.EventLog;
import game.items.AccessCardLevel1;
import game.items.AccessCardLevel2;
import game.items.AccessCardLevel3;
import game.items.FirstAidKit;
import game.items.PlasmaCutter;
import game.items.SterilisationBox;
import game.quota.QuotaListener;
import game.quota.QuotaManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The ground which an Actor can interact with to buy Buyables,
 * sell Sellables, deposit Depositables, and enforces the Quota Cycle.
 *
 * @author Tommy
 * @author Mary
 */
public class Supercomputer extends Ground implements QuotaListener {

    private final List<Supplier<Buyable>> buyables = new ArrayList<>();
    private boolean deadlineActive = false;

    /**
     * Constructor. Registers this Supercomputer as a quota listener and adds
     * the Plasma Cutter to the shop alongside existing buyables.
     */
    public Supercomputer() {
        super('≡', "Supercomputer");

        // Existing buyables — unchanged
        buyables.add(FirstAidKit::new);
        buyables.add(SterilisationBox::new);
        buyables.add(AccessCardLevel1::new);
        buyables.add(AccessCardLevel2::new);
        buyables.add(AccessCardLevel3::new);
        buyables.add(PlasmaCutter::new);
        QuotaManager.getInstance().addListener(this);
    }

    /**
     * The supercomputer cannot be stepped on.
     *
     * @param actor the Actor to check
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each game turn: advance the quota clock and fire adjacent workers
     * if the deadline was missed on the previous tick.
     *
     * @param location the location of this supercomputer tile
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        QuotaManager.getInstance().tick();
        System.out.println("[QUOTA STATUS] " + QuotaManager.getInstance());

        if (deadlineActive) {
            fireAdjacentWorkers(location);
            deadlineActive = false;
        }
    }

    /**
     * If the player is near the Supercomputer, they are given the option
     * to purchase all the preset Buyables, sell any Sellables, and
     * deposit any Depositables in their inventory.
     *
     * @param actor     the Actor standing nearby
     * @param location  the location of this Supercomputer
     * @param direction the direction of the Supercomputer from the actor
     * @return a list of allowable actions for this Supercomputer
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actionList = new ActionList();

        if (actor.hasStatistic(ActorStatistics.CREDITS)) {
            for (Supplier<Buyable> supplier : buyables)
                actionList.add(new BuyAction(supplier.get()));

            for (Sellable sellable : actor.getInventory().getItemsAs(Sellable.class))
                actionList.add(new SellAction(sellable));

            //new deposit actions
            for (Depositable depositable : actor.getInventory().getItemsAs(Depositable.class))
                actionList.add(new DepositAction(depositable));
        }

        return actionList;
    }

    //quota listener
    @Override
    public void onQuotaMet(int newRank, int newTarget, int newTurnLimit, String message) {
        EventLog.getInstance().log(message);
        System.out.println(message);
    }

    @Override
    public void onDeadlineMissed(String message) {
        EventLog.getInstance().log(message);
        System.out.println(message);
        deadlineActive = true;
    }

    /**
     * Makes unconscious any worker standing on a tile adjacent to this
     * Supercomputer when the deadline is missed.
     */
    private void fireAdjacentWorkers(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                Actor worker = adjacent.getActor();
                if (worker.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                    worker.unconscious(location.map());
                    String msg = "[FIRED] The Supercomputer fires " + worker
                            + " — they collapse, unconscious!";
                    EventLog.getInstance().log(msg);
                    System.out.println(msg);
                }
            }
        }
    }
}