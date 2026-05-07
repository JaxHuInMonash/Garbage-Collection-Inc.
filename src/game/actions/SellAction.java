package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.capabilities.Ability;
import game.capabilities.Sellable;

public class SellAction extends Action {
    private final Sellable sellable;

    public SellAction(Sellable sellable) {
        this.sellable = sellable;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasStatistic(ActorStatistics.CREDITS))
            return actor + " is incapable of selling";

        return sellable.sell(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells " + sellable;
    }
}

