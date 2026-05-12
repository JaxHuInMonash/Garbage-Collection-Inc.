package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
/**
 * A rusted iron door requiring Clearance Level 2 or higher to open.
 * Displays as N.
 * Unlocking this door causes the heavy machinery to overheat, instantly
 * setting all adjacent floor tiles on Fire for 2 turns. Walking through
 * the fire applies the standard burn effect.
 * @author zhengjun Yang
 */
public class IronDoor extends SecuredDoor {
    /**
     * Constructor.
     */
    public IronDoor() {
        super('N', "Iron Door");
    }
    /**
     * Returns the minimum clearance level required: 2.
     *
     * @return 2
     */
    @Override
    protected int requiredClearance() { return 2; }
    /**
     * Sets all adjacent non-wall, non-door tiles on fire for 2 turns.
     *
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of the unlock effect
     */
    @Override
    public String unlockEffect(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!ground.hasAbility(Ability.BLOCKS_ENTRY) && !ground.hasAbility(Ability.ON_FIRE)) {
                adjacent.setGround(new Fire(ground, 2));
            }
        }
        return actor + " unlocked the Iron Door — machinery overheats, adjacent tiles catch fire!";
    }
}