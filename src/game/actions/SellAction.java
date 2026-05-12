package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.capabilities.Ability;
import game.capabilities.Sellable;
/**
 * An action that sells any item implementing the {@link game.capabilities.Sellable}
 * interface to the Supercomputer.
 * <p>
 * This action depends on the {@link game.capabilities.Sellable} interface rather
 * than any concrete item class, following the Dependency Inversion and
 * Interface Segregation principles. Any new sellable item can reuse this action
 * without creating a new action class.
 *@author zhengjun Yang
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
     * Sells the item to the Supercomputer if the actor has the CREDITS statistic.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasStatistic(ActorStatistics.CREDITS))
            return actor + " is incapable of selling";

        return sellable.sell(actor, map);
    }
    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells " + sellable;
    }
}

