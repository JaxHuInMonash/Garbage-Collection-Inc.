package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.statuses.ReflectionStatus;
import game.penalty.FriendlyFireTrigger;
import java.util.List;

/**
 * An action that allows one actor to attack another using their intrinsic weapon.
 * <p>
 * The attacker's {@link IntrinsicWeapon} handles hit chance, damage calculation,
 * and the attack description. If the target becomes unconscious after the attack,
 * the target is removed from the map.
 *also checks for friendly fire
 * {@link FriendlyFireTrigger} after each attack.
 * @author Jax
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
        FriendlyFireTrigger.checkFriendlyFire(actor, target, map);
        // Reflection: if the target absorbed damage via ReflectionStatus,
        // deal the absorbed amount back to the attacker.
        result += applyReflection(actor);

        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }

        if (!actor.isConscious()) {
            result += "\n" + actor.unconscious(target, map);
        }

        return result;
    }

    /**
     * Checks for pending reflected damage on the target and applies it back
     * to the attacker. Cleans up the {@link ReflectionStatus} if it is fully
     * consumed after the reflection is applied.
     *
     * @param attacker the actor who performed the attack
     * @return a description of the reflection, or empty string if none
     */
    private String applyReflection(Actor attacker) {
        List<ReflectionStatus> reflections = target.statusesOf(ReflectionStatus.class);
        StringBuilder sb = new StringBuilder();
        for (ReflectionStatus ref : reflections) {
            int reflected = ref.consumePendingReflection();
            if (reflected > 0) {
                attacker.hurt(reflected);
                sb.append("\n").append(target).append(" reflects ").append(reflected)
                        .append(" damage back at ").append(attacker).append("!");
            }
            if (!ref.isStatusActive()) {
                target.removeStatus(ref);
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
