package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for entities (ground tiles or items) that can be cut
 * by the {@link game.items.PlasmaCutter}.
 *
 * <p>Using an interface rather than instanceof checks eliminates the
 * Conditional Complexity design smell and satisfies OCP: new cuttable
 * targets are added by implementing this interface, never by editing
 * {@link game.actions.CutAction}.
 *
 * @author Mary
 */
public interface Cuttable {

    /**
     * Called when the Plasma Cutter is used on this target.
     * Implementations handle all cut consequences: item drops, tile
     * transformation, creature spawning, status effects, etc.
     *
     * @param actor          the actor performing the cut
     * @param targetLocation the location of this target
     * @param map            the current game map
     * @return a human-readable description of what happened
     */
    String onCut(Actor actor, Location targetLocation, GameMap map);
}