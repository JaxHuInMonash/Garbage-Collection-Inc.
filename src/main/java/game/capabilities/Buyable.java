package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The interface for any object that can be bought by an actor.
 * <p>
 * The buy protocol is split into two polymorphic steps so callers
 * (e.g. {@link game.actions.BuyAction}) never have to introspect the
 * concrete type of either the buyable or the inventory:
 * <ol>
 *     <li>{@link #attemptAcquireBy(Actor)} places the buyable with the actor
 *         (typically by adding to the actor's inventory) and reports whether
 *         the placement succeeded. Returning {@code false} means the buy is
 *         cancelled — credits are not charged and {@link #onBought} is not
 *         called.</li>
 *     <li>{@link #onBought(Actor, GameMap)} runs only after a successful
 *         acquisition and applies any side effects (damage, healing, hidden
 *         fees, item destruction, etc.).</li>
 * </ol>
 * Splitting the protocol this way removes the need for {@code instanceof}
 * checks in {@code BuyAction} — the concrete buyable encapsulates how it
 * places itself with the buyer, and the engine's polymorphic
 * {@link edu.monash.fit2099.engine.items.Inventory#add(edu.monash.fit2099.engine.items.Item)}
 * handles any capacity constraints (e.g. weight limits) without the caller
 * needing to know what kind of inventory the actor has.
 *
 * @author Tommy
 * @author Jax
 */
public interface Buyable {

    /**
     * Called when the actor lacks the funds to purchase this buyable.
     * Allows item-specific fallbacks (e.g. the First Aid Kit killing the buyer).
     *
     * @author Tommy
     */
    default String onInsufficientCredits(Actor actor, GameMap map) {
        return ".";
    }

    /**
     * Attempts to place this buyable with the actor. The default implementation
     * always succeeds and is appropriate for buyables that exist outside an
     * inventory (e.g. a permanent service or upgrade). Item-based buyables
     * override this to delegate to {@code actor.getInventory().add(this)},
     * letting the inventory's own capacity logic decide.
     *
     * @param actor the prospective buyer
     * @return true if the buyable was placed; false if the actor cannot accept
     *         it (e.g. weight limit exceeded)
     */
    default boolean attemptAcquireBy(Actor actor) {
        return true;
    }

    /**
     * Applies any side effects of a successful purchase. Called only after
     * {@link #attemptAcquireBy(Actor)} has returned {@code true} and credits
     * have been deducted. Implementations must not re-add themselves to the
     * inventory here — that responsibility belongs to {@code attemptAcquireBy}.
     *
     * @param actor actor that bought the Buyable
     * @param map   map the Buyable was bought on
     * @return resultant message of buying the Buyable
     */
    String onBought(Actor actor, GameMap map);

    /**
     * Provides the value of the Buyable
     * @return integer value of the Buyable's buy value
     */
    int getBuyValue();
}
