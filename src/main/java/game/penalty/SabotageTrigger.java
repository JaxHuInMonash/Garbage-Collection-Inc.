package game.penalty;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.capabilities.Ability;
import game.display.EventLog;
/**
 * Implements {@link InfractionTrigger}.
 * <p>
 * RETROFITTED into {@link game.actions.UseAlienCubeAction}: fires when a
 * {@link ContractedWorker} uses an AlienCube, corrupting surrounding terrain
 * into ToxicWaste. This is treated as deliberate sabotage of the facility.
 * Increments the Worker's infraction level via {@link InfractionManager} and
 * triggers an {@link game.actors.EnforcerBot} spawn if the level reaches 1 or above.
 *
 * @author Zhengjun Yang
 */
public class SabotageTrigger implements InfractionTrigger {
    /**
     * Increments the violating Worker's infraction level if the actor is a
     * ContractedWorker.
     *
     * @param violator the Actor being checked
     * @param map      the current GameMap
     */
    @Override
    public void checkAndApply(Actor violator, GameMap map) {
        if (!violator.hasAbility(Ability.IS_WORKER)) return;
        EventLog.getInstance().log("[INFRACTION] " + violator + " sabotaged the facility!");
        InfractionManager.getInstance().increment(violator, map);
    }
}
