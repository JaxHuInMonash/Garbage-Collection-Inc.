package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
/**
 * Implements {@link InfractionTrigger}.
 * <p>
 * Fires when a {@link ContractedWorker} steps onto a
 * {@link game.grounds.RestrictedGround} tile. Increments the Worker's
 * infraction level by 1 via {@link InfractionManager} and triggers an
 * {@link game.actors.EnforcerBot} spawn if the level reaches 1 or above.
 *
 * @author Zhengjun Yang
 */
public class ZoneTrespassTrigger implements InfractionTrigger {
    private final String zoneName;
    /**
     * Constructor.
     *
     * @param zoneName the name of the restricted zone
     */
    public ZoneTrespassTrigger(String zoneName) { this.zoneName = zoneName; }
    /**
     * Increments the violating Worker's infraction level if the actor is a
     * ContractedWorker.
     *
     * @param violator the Actor being checked
     * @param map      the current GameMap
     */
    @Override
    public void checkAndApply(Actor violator, GameMap map) {
        if (!(violator instanceof ContractedWorker)) return;
        System.out.println("[INFRACTION] " + violator + " trespassed into: " + zoneName);
        InfractionManager.getInstance().increment(violator, map);
    }
}