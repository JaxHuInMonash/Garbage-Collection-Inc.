package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.capabilities.Buyable;

/**
 * An Action that allows Actors with CREDITS to buy {@link Buyable} items.
 * <p>
 * The buy is rejected (no credits charged, no side effects fired) when the
 * actor cannot accept the buyable. The acquisition check itself is delegated
 * polymorphically to {@link Buyable#attemptAcquireBy(Actor)} — this action
 * therefore makes no assumptions about whether the buyable is an inventory
 * item or what kind of inventory the actor uses, avoiding {@code instanceof}
 * checks entirely.
 *
 * @author Tommy
 * @author Jax
 */
public class BuyAction extends Action {

    private final Buyable buyable;

    /**
     * Constructor.
     *
     * @param buyable the buyable to acquire
     */
    public BuyAction(Buyable buyable) {
        this.buyable = buyable;
    }

    /**
     * Executes the buy sequence. Order of operations:
     * <ol>
     *   <li>Check the actor has enough credits.</li>
     *   <li>Ask the buyable to place itself with the actor.</li>
     *   <li>Deduct credits.</li>
     *   <li>Apply the buyable's purchase side effects.</li>
     * </ol>
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is on
     * @return a description of the buy result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        int credits = actor.getStatistic(ActorStatistics.CREDITS);
        int cost = buyable.getBuyValue();

        if (credits < cost) {
            String appendMessage = buyable.onInsufficientCredits(actor, map);
            return actor + " attempted to buy " + buyable + " but lacked credits. " +
                    "(" + credits + "/" + cost + ")" + appendMessage;
        }

        if (!buyable.attemptAcquireBy(actor)) {
            return actor + " attempted to buy " + buyable + " but couldn't accept it (inventory full).";
        }

        actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, cost);
        return buyable.onBought(actor, map);
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
