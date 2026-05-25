package game;

/**
 * An enumeration of statistics that items can track.
 * Used with the engine's statistic system for weight limits,
 * commerce, quantities, and access control.
 *
 * @author Jax
 * @author Tommy
 */
public enum ItemStatistics {
    /** The weight of the item, used for inventory weight limits. */
    WEIGHT,

    /** The remaining uses or charges of a multi-use item. */
    QUANTITY,

    /** The credit value when buying this item from the Supercomputer. */
    BUY_VALUE,

    /** The credit value when selling this item to the Supercomputer. */
    SELL_VALUE,

    /** The clearance level granted by an access card. */
    CLEARANCE
}
