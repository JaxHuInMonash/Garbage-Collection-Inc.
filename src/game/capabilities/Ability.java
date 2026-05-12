package game.capabilities;

/**
 * An enumeration of abilities that can be granted to game entities.
 * Abilities represent permissions or capabilities that allow entities
 * to perform specific actions in the game world.
 *
 * @author Jax
 */
public enum Ability {
    /**
     * Allows an actor (or an item granting this ability) to unlock doors.
     */
    CAN_UNLOCK_DOORS,

    /**
     * Indicates the carrier has a SterilisationBox, which purifies consumables
     * and environmental hazards on the moon.
     */
    HAS_STERILISATION,

    /**
     * Marks an actor as a valid target for creature attacks (e.g., Undead).
     * Granted to ContractedWorkers so that hostile creatures can identify
     * targets without depending on concrete worker classes.
     */
    ATTACKABLE_BY_CREATURES,

    IS_UNDEAD,
    BLOCKS_ENTRY,
    IS_MAGIC_CIRCLE,
    ON_FIRE,
    IS_TOXIC,
    HAS_CLEARANCE,
    }

