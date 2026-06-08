package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.capabilities.Ability;
import game.display.EventLog;
/**
 * Implements {@link InfractionTrigger}.
 * <p>
 * RETROFITTED into {@link game.actions.AttackAction}: fires when a
 * {@link ContractedWorker} attacks another ContractedWorker. Increments the
 * attacker's infraction level via {@link InfractionManager} and triggers an
 * {@link game.actors.EnforcerBot} spawn if the level reaches 1 or above.
 * <p>
 * The static convenience method {@link #checkFriendlyFire(Actor, Actor, GameMap)}
 * should be called inside {@code AttackAction.execute()} before dealing damage.
 * Worker identity is detected via the {@link Ability#IS_WORKER} capability
 * rather than {@code instanceof}, so any future worker subclass automatically
 * participates without changes here (OCP).
 *
 * @author Zhengjun Yang
 */
public class FriendlyFireTrigger implements InfractionTrigger {
    /**
     * Increments the violating Worker's infraction level if the actor has
     * the {@link Ability#IS_WORKER} capability.
     *
     * @param violator the Actor being checked
     * @param map      the current GameMap
     */
    @Override
    public void checkAndApply(Actor violator, GameMap map) {
        if (!violator.hasAbility(Ability.IS_WORKER)) return;
        EventLog.getInstance().log("[INFRACTION] " + violator + " committed friendly fire!");
        InfractionManager.getInstance().increment(violator, map);
    }
    /**
     * Convenience method to be called inside {@code AttackAction.execute()}.
     * Only fires the trigger when both the attacker and target carry the
     * {@link Ability#IS_WORKER} capability.
     *
     * @param attacker the attacking actor
     * @param target   the actor being attacked
     * @param map      the current GameMap
     */
    public static void checkFriendlyFire(Actor attacker, Actor target, GameMap map) {
        if (attacker.hasAbility(Ability.IS_WORKER) && target.hasAbility(Ability.IS_WORKER)) {
            new FriendlyFireTrigger().checkAndApply(attacker, map);
        }
    }
}
