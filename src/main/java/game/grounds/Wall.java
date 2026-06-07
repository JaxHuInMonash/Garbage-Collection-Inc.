package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.Ability;
/**
 * A class representing a solid wall. No actor can enter a wall.
 *
 * @author Adrian Kristanto
 */
public class Wall extends Ground {
    public Wall() {
        super('#', "Wall");
        this.enableAbility(Ability.BLOCKS_ENTRY);
        this.enableAbility(Ability.IS_WALL);
    }

    /**
     * Walls are impassable. No actor can enter a wall.
     *
     * @param actor the Actor to check
     * @return always false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Walls block thrown objects.
     *
     * @return true
     */
    @Override
    public boolean blocksThrownObjects() {
        return true;
    }
}
