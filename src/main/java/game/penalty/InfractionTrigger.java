package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
/**
 * Interface for any game event that can cause a Worker to incur an infraction.
 * <p>
 * Implementors call {@link InfractionManager} to increment the violating Worker's
 * infraction level and conditionally spawn an {@link game.actors.EnforcerBot}.
 * Three concrete triggers exist: trespassing into a restricted zone, committing
 * friendly fire, and stealing another Worker's equipment.
 *
 * @author Zhengjun Yang
 */
public interface InfractionTrigger {
    /**
     * Check whether the given actor has committed a violation and apply the infraction if so.
     *
     * @param violator the Actor being checked
     * @param map      the current GameMap
     */
    void checkAndApply(Actor violator, GameMap map);
}