package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Ability;
import game.statuses.PoisonStatus;

/**
 * An action to drink directly from a Puddle on the moon's surface.
 * <p>
 * If the actor has a SterilisationBox (via {@link Ability#HAS_STERILISATION}),
 * the water is purified and heals 1 HP. Otherwise, the water is toxic and
 * poisons the drinker for 1 damage per turn for 3 turns.
 *
 * @author Jax
 */
public class DrinkPuddleAction extends Action {

    /**
     * Constructor.
     */
    public DrinkPuddleAction() {
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            actor.heal(1);
            return actor + " drinks purified water from the puddle and heals 1 HP.";
        } else {
            actor.addStatus(new PoisonStatus(1, 3));
            return actor + " drinks toxic water from the puddle and is poisoned! (1 dmg/turn for 3 turns)";
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " drinks from Puddle";
    }
}
