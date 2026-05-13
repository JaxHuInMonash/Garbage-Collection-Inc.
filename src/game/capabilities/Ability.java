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
     * Allows an item to unlock secured doors when used by an actor.
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

    /**
     * Marks a ground tile as impassable (e.g., walls, locked doors).
     */
    BLOCKS_ENTRY,

    /**
     * Marks a ground tile as a Magic Circle teleportation point.
     */
    IS_MAGIC_CIRCLE,

    /**
     * Marks a ground tile as currently on fire.
     */
    ON_FIRE,

    /**
     * Marks a ground tile as toxic waste that damages actors standing on it.
     */
    IS_TOXIC,

    /**
     * Marks a ground tile as a Hole spawner.
     */
    IS_HOLE,
}
