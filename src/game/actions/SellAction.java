package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Sellable;

/**
 * An Action that allows Actors with CREDITS to sell Sellables
 *
 * @author Tommy
 */
public class SellAction extends Action {
    private final Sellable sellable;

    /**
     * CONSTRUCTOR
     * @param sellable Sellable object
     */
    public SellAction(Sellable sellable) {
        this.sellable = sellable;
    }

    /**
     * Executes the sell sequence for the respective Sellable object,
     * awarding the Actor the Sell value of the Sellable object
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a description of the sell result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.INCREASE, sellable.getSellValue()); // Capped at 1000

        return sellable.onSold(actor, map);
    }

    /**
     * Returns a menu description for this sell action.
     *
     * @param actor the actor performing the sell request
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells " + sellable + " for " + sellable.getSellValue() + " credits.";
    }
}
