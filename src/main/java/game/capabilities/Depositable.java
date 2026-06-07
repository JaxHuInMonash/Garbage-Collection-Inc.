package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Capability interface for items that can be deposited at the Supercomputer
 * in exchange for <em>Company Credits</em> (contributing toward the quota).
 *
 * <p>Mirroring the existing {@link Sellable} interface (connascence of
 * convention) keeps the deposit pathway symmetric: {@code DepositAction}
 * calls {@link #getDepositValue()} and {@link #onDeposited(Actor, GameMap)}
 * exactly as {@code SellAction} calls the sell equivalents.
 *
 * <p>Items that can be both sold <em>and</em> deposited implement both
 * {@code Depositable} and {@link Sellable}; the Supercomputer exposes
 * separate actions for each choice (the worker chooses one, consuming the item).
 *
 * @author Mary
 */
public interface Depositable {

    /**
     * Called when the item is deposited at the Supercomputer.
     * Implementations must remove the item from the actor's inventory and
     * apply any side effects (damage, healing, teleportation, etc.).
     *
     * @param actor the actor depositing the item
     * @param map   the map the actor is currently on
     * @return a human-readable description of what happened
     */
    String onDeposited(Actor actor, GameMap map);

    /**
     * Returns the Company Credit value awarded for depositing this item.
     *
     * @return the deposit value in Company Credits
     */
    int getDepositValue();
}