package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.ItemStatistics;
import game.capabilities.Buyable;

/**
 * An Action that allows Actors with CREDITS to buy Buyables
 *
 * @author Tommy
 */
public class BuyAction extends Action {
    private final Buyable buyable;

    /**
     * Constructor
     *
     * @param buyable Item to buy
     */
    public BuyAction(Buyable buyable) {
        this.buyable = buyable;
    }

    /**
     * Executes the buy sequence for the respective Buyable object,
     * deducting the Actor the buy value of the Buyable object
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a description of the sell result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        int credits = actor.getStatistic(ActorStatistics.CREDITS);
        int cost = buyable.getBuyValue();

        if (credits < cost) {
            String appendMessage = buyable.onInsufficientCredits(actor, map);

            return actor + " attempted to buy " + this + " but lacked credits. " +
                    "(" + credits + "/" + cost + ")" + appendMessage;
        }

        actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, cost);

        return buyable.buy(actor, map);
    }

    /**
     * Returns a menu description for this buy action.
     *
     * @param actor the actor performing the buy request
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " buys " + buyable + " for " + buyable.getBuyValue() + " credits.";
    }
}
