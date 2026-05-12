package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ActorStatistics;
import game.actions.BuyAction;
import game.actions.SellAction;
import game.capabilities.Buyable;
import game.capabilities.Sellable;
import game.items.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The ground which an Actor can interact with to buy Buyables
 * and sell Sellables
 *
 * @author Tommy
 */
public class Supercomputer extends Ground {
    List<Supplier<Buyable>> buyables = new ArrayList<>();

    /**
     * Constructor, initiates the buyables Arraylist with some default Buyables
     * for actors to purchase later in the future
     */
    public Supercomputer() {
        super('≡', "Supercomputer");

        buyables.add(FirstAidKit::new);
        buyables.add(SterilisationBox::new);
        buyables.add(AccessCardLevel1::new);
        buyables.add(AccessCardLevel2::new);
        buyables.add(AccessCardLevel3::new);
    }

    /**
     * The supercomputer cannot be stepped on,
     * so it doesn't allow actors to enter
     *
     * @param actor the Actor to check
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {return false;}

    /**
     * If the player is near the Supercomputer, they are given the option
     * to purchase all the preset Buyables and sell any Sellables in their inventory
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
        }

        return actionList;
    }
}
