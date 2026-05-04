package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.ActorStatistics;
import game.capabilities.Buyable;

public class BuyAction extends Action {
    private final Buyable buyable;

    public BuyAction(Buyable buyable) {
        this.buyable = buyable;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasStatistic(ActorStatistics.CREDITS)) {
            return actor + " is incapable of buying";
        }

        return buyable.buy(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " buys " + buyable + " for " + buyable.getValue() + " credits.";
    }
}
