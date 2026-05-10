package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ActorStatistics;
import game.actions.BuyAction;
import game.actions.SellAction;
import game.capabilities.Ability;
import game.capabilities.Buyable;
import game.capabilities.Sellable;
import game.items.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * While other classes get to be security doors, mysterious flasks, or highly
 * stressed contracted workers, this class humbly accepts its role as
 * the thing everyone walks all over.
 *
 * @author Adrian Kristanto
 */
public class Supercomputer extends Ground {
    List<Supplier<Buyable>> buyables = new ArrayList<>();

    public Supercomputer() {
        super('≡', "Supercomputer");

        buyables.add(FirstAidKit::new);
        buyables.add(SterilisationBox::new);
        buyables.add(AccessCardLevel1::new);
        buyables.add(AccessCardLevel2::new);
        buyables.add(AccessCardLevel3::new);
    }

    @Override
    public boolean canActorEnter(Actor actor) {return false;}

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
