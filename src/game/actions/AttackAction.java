package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * An action that allows one actor to attack another using their intrinsic weapon.
 * <p>
 * The attacker's {@link IntrinsicWeapon} handles hit chance, damage calculation,
 * and the attack description. If the target becomes unconscious after the attack,
 * the target is removed from the map.
 *
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
     *
     * @param actor the actor performing the attack
     * @param map   the game map the actor is on
     * @return a description of the attack result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        IntrinsicWeapon weapon = actor.getIntrinsicWeapon();
        String result = weapon.attack(actor, target, map);

        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }

        return result;
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
