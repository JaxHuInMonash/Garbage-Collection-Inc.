package game.capabilities;

/**
 * An enumeration of abilities (capability markers) granted to game entities.
 * <p>
 * Values are grouped by the kind of entity that typically owns them. The
 * engine's {@code enableAbility(Enum)} accepts any enum, so a single enum is
 * kept here to minimise call-site churn — the grouping is communicated via
 * the doc-comment regions below.
 *
 * @author Jax
 */
public enum Ability {

    // ──────────────────────────────────────────────────────────────────
    // Item abilities
    // ──────────────────────────────────────────────────────────────────

    /** Allows an item to unlock secured doors when used by an actor. */
    CAN_UNLOCK_DOORS,

    /**
     * Indicates the carrier has a SterilisationBox, which purifies consumables
     * and environmental hazards on the moon.
     */
    HAS_STERILISATION,

    // ──────────────────────────────────────────────────────────────────
    // Actor abilities (creature / role markers)
    // ──────────────────────────────────────────────────────────────────

    /**
     * Marks an actor as a valid target for creature attacks (e.g., Undead).
     * Granted to ContractedWorkers so that hostile creatures can identify
     * targets without depending on concrete worker classes.
     */
    ATTACKABLE_BY_CREATURES,

    /**
     * Marks an actor as a Parasite. Used by states to detect Parasite presence
     * without coupling to the concrete Parasite class.
     */
    IS_PARASITE,

    /**
     * Marks an actor as an Undead creature. Used by states to count Undead
     * without coupling to the concrete Undead class.
     */
    IS_UNDEAD,

    /**
     * Marks an actor as hostile to Workers (e.g. Undead, SecurityDrone).
     * Used by the Swarmling's CullingState to detect nearby threats.
     */
    HOSTILE_TO_WORKERS,

    /**
     * Marks an actor as a {@link game.actors.ContractedWorker} (or future
     * worker subclass). Used by the penalty system and worker-on-worker
     * action plumbing to detect "this is a company worker" without using
     * {@code instanceof}, so any future worker subclass automatically
     * participates (OCP).
     */
    IS_WORKER,

    /** Marks an actor as one of the Meeple Clan. */
    IS_MEEPLE,

    /**
     * Allows an actor to pass through {@link game.grounds.SecuredDoor} tiles
     * regardless of lock state. Granted to {@link game.actors.EnforcerBot}.
     */
    CAN_PASS_DOORS,

    // ──────────────────────────────────────────────────────────────────
    // Ground abilities (terrain markers)
    // ──────────────────────────────────────────────────────────────────

    /** Marks a ground tile as impassable (e.g., walls, locked doors). */
    BLOCKS_ENTRY,

    /**
     * Marks a ground tile as the Supercomputer. Used by ground-aware items
     * (e.g. Industrial Fan side-effects) to locate the Supercomputer without
     * coupling to the concrete class.
     */
    IS_SUPERCOMPUTER,

    /**
     * Marks an item as a cutter (e.g. Plasma Cutter). Used to detect that
     * the holder is carrying a cutting tool without coupling to the concrete
     * class — the {@link game.capabilities.Cuttable} ground tile or item
     * stays decoupled from any specific cutter implementation.
     */
    CAN_CUT,

    /** Marks a ground tile as a Magic Circle teleportation point. */
    IS_MAGIC_CIRCLE,

    /** Marks a ground tile as currently on fire. */
    ON_FIRE,

    /** Marks a ground tile as toxic waste that damages actors standing on it. */
    IS_TOXIC,

    /** Marks a ground tile as a Hole spawner. */
    IS_HOLE,

    /**
     * Marks a ground tile as a creature spawner (Hole or Vent). Used by the
     * Surveyor Optics upgrade to detect what to reveal, without coupling to
     * the concrete spawner classes.
     */
    IS_SPAWNER,

    /**
     * Marks a ground tile as a Wall. Used by the Phase Stabilizer upgrade to
     * locate walls the worker may phase through, without coupling to the
     * concrete {@code Wall} class.
     */
    IS_WALL,

    /** Marks a ground tile that can be flooded by weather (e.g. Dirt). */
    FLOODABLE,

    /** Marks a ground tile that can evaporate in dry weather (e.g. Puddle). */
    EVAPORATABLE,

    // ──────────────────────────────────────────────────────────────────
    // Weather condition abilities
    // ──────────────────────────────────────────────────────────────────

    /** Marks a weather condition that can extinguish fires (rain, drizzle, etc.). */
    EXTINGUISH,

}
