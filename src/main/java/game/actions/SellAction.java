package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.capabilities.Sellable;

/**
 * An action that sells any item implementing the {@link Sellable}
 * interface to the Supercomputer.
 * <p>
 * This action handles the credit transfer (adding {@link Sellable#getSellValue()}
 * to the actor's CREDITS statistic), then delegates item-specific side effects
 * to {@link Sellable#onSold(Actor, GameMap)}.
 * <p>
 * This design depends on the {@link Sellable} abstraction rather than any
 * concrete item class, following the Dependency Inversion and Interface
 * Segregation principles.
 *
 * @author zhengjun Yang
 */
public class SellAction extends Action {

    private final Sellable sellable;

    /**
     * Constructor.
     *
     * @param sellable the Sellable item to sell
     */
    public SellAction(Sellable sellable) {
        this.sellable = sellable;
    }

    /**
     * Sells the item: adds credits to the actor, then applies the item's
     * sell side effects. Requires the actor to have the CREDITS statistic.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasStatistic(ActorStatistics.CREDITS)) {
            return actor + " is incapable of selling.";
        }

        int value = sellable.getSellValue();
        actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.INCREASE, value);
        return sellable.onSold(actor, map);
    }

    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells " + sellable + " (" + sellable.getSellValue() + " credits)";
    }
}
