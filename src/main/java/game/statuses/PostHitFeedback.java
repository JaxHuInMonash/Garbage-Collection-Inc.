package game.statuses;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Capability extension of {@link Status} for status effects that wish to
 * respond <em>after</em> their bearer is hit in combat.
 * <p>
 * The engine's {@link Actor#hurt(int)} hook only knows the damage amount —
 * <strong>it never receives the attacker</strong>. But the attacker IS known
 * inside {@link game.actions.AttackAction}, after the weapon resolves. This
 * interface bridges those two phases:
 * <ol>
 *   <li>During {@link Actor#hurt(int)} the status may stage a feedback
 *       (e.g. reflection accumulates pending reflect damage, retaliation rolls
 *       its trigger chance, …);</li>
 *   <li>Once the weapon resolves, {@link game.actions.AttackAction} iterates
 *       every {@code PostHitFeedback} on the target and calls
 *       {@link #onTargetHit(Actor, Actor, GameMap)} so each status can apply
 *       its feedback to the attacker.</li>
 * </ol>
 * Extending {@link Status} means existing engine machinery —
 * {@code statusesOf}, {@code tickStatuses}, {@code isStatusActive} — keeps
 * working unchanged. AttackAction depends only on this interface, so new
 * post-hit effects can be added without touching it (Open-Closed).
 *
 * @author Jax
 */
public interface PostHitFeedback extends Status {

    /**
     * Apply this status's feedback to the attacker who just hit the victim.
     *
     * @param attacker the actor that dealt the hit
     * @param victim   the actor wearing this status
     * @param map      the current game map
     * @return a human-readable description of what happened, or an empty
     *         string if this status produced no feedback this hit
     */
    String onTargetHit(Actor attacker, Actor victim, GameMap map);
}
