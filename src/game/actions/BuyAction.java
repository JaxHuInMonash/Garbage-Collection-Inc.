package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.ItemStatistics;
import game.capabilities.Buyable;

public class BuyAction extends Action {
    private final Buyable buyable;

    public BuyAction(Buyable buyable) {
        this.buyable = buyable;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.getStatistic(ActorStatistics.CREDITS) < buyable.getBuyValue()) {
            String suffix = buyable.onInsufficientCredits(actor, map);

            return actor + " attempted to buy " + this + " but couldn't afford it. ("
                    + actor.getStatistic(ActorStatistics.CREDITS) + "/"
                    + buyable.getBuyValue() + ")" + suffix;
        }

        actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, buyable.getBuyValue());

        return buyable.buy(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " buys " + buyable + " for " + buyable.getBuyValue() + " credits.";
    }
}
