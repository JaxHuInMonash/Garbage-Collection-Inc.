package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.penalty.FriendlyFireTrigger;
import game.statuses.PostHitFeedback;

import java.util.ArrayList;

/**
 * An action that allows one actor to attack another using their intrinsic weapon.
 * <p>
 * The attacker's {@link IntrinsicWeapon} handles hit chance, damage calculation,
 * and the attack description. After the weapon resolves:
 * <ol>
 *   <li>Every {@link PostHitFeedback} status on the target is queried
 *       polymorphically — this is what lets {@link game.statuses.ReflectionStatus}
 *       (Swarmling) and {@link game.statuses.RetaliationStatus} (Fighter T1
 *       upgrade) compose without this action knowing their concrete types.</li>
 *   <li>{@link FriendlyFireTrigger#checkFriendlyFire(Actor, Actor, GameMap)}
 *       (REQ4) checks whether the attacker has just committed friendly fire
 *       against another worker — incrementing infraction level if so and
 *       potentially spawning an {@link game.actors.EnforcerBot}.</li>
 *   <li>If either combatant becomes unconscious, they are removed via the
 *       engine's death lifecycle.</li>
 * </ol>
 *
 * @author Jax
 * @author Zhengjun
 */
public class AttackAction extends Action {

    private final Actor target;

    /**
     * Constructor.
     *
     * @param target the actor to attack
     */
    public AttackAction(Actor target) {
        this.target = target;
    }

    /**
     * Executes the attack using the attacker's intrinsic weapon, then walks
     * every {@link PostHitFeedback} on the target so each one may apply its
     * own response to the attacker. If either combatant becomes unconscious,
     * they are removed via the engine's death lifecycle.
     *
     * Executes the attack using the attacker's intrinsic weapon. If the
     * target becomes unconscious, they are removed from the map.
     *after the attack resolves, calls
     * {@link FriendlyFireTrigger#checkFriendlyFire(Actor, Actor, GameMap)} to
     * check whether the attacker has committed friendly fire against a fellow
     * {@link game.actors.ContractedWorker}, incrementing their infraction level
     * if so and potentially spawning an {@link game.actors.EnforcerBot}.
     * @param actor the actor performing the attack
     * @param map   the game map the actor is on
     * @return a description of the attack result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        IntrinsicWeapon weapon = actor.getIntrinsicWeapon();
        String result = weapon.attack(actor, target, map);

        // Polymorphic post-hit feedback (ReflectionStatus, RetaliationStatus, future effects).
        // ReflectionStatus already implements PostHitFeedback, so the old hand-rolled
        // applyReflection() it was paired with at A2 time has been removed.
        result += applyPostHitFeedback(actor, map);

        // REQ4 — worker-on-worker attacks bump the attacker's infraction level.
        FriendlyFireTrigger.checkFriendlyFire(actor, target, map);

        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }

        if (!actor.isConscious()) {
            result += "\n" + actor.unconscious(target, map);
        }

        return result;
    }

    /**
     * Iterates every {@link PostHitFeedback} on the target and applies it to
     * the attacker. Snapshots the list first because a feedback may remove
     * itself (e.g. an exhausted {@link game.statuses.ReflectionStatus}).
     *
     * @param attacker the actor who performed the attack
     * @param map      the current game map
     * @return concatenated feedback messages, or empty string if none fired
     */
    private String applyPostHitFeedback(Actor attacker, GameMap map) {
        StringBuilder sb = new StringBuilder();
        for (PostHitFeedback feedback : new ArrayList<>(target.statusesOf(PostHitFeedback.class))) {
            String msg = feedback.onTargetHit(attacker, target, map);
            if (msg != null && !msg.isEmpty()) {
                sb.append("\n").append(msg);
            }
            if (!feedback.isStatusActive()) {
                target.removeStatus(feedback);
            }
        }
        return sb.toString();
    }

    /**
     * Returns a menu description for this attack action.
     *
     * @param actor the actor performing the attack
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target;
    }
}
