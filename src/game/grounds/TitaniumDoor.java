package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
/**
 * A titanium door requiring Clearance Level 3 to open.
 * Displays as {@code M}.
 * <p>
 * Unlocking this door triggers a localised decontamination sequence,
 * instantly healing the worker for 5 health points.
 *
 * @author zhengjun Yang
 */
public class TitaniumDoor extends SecuredDoor {
    /**
     * Constructor.
     */
    public TitaniumDoor() {
        super('M', "Titanium Door");
    }
    /**
     * Returns the minimum clearance level required: 3.
     *
     * @return 3
     */
    @Override
    protected int requiredClearance() { return 3; }
    /**
     * Heals the actor for 5 HP via decontamination sequence.
     *
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of the unlock effect
     */
    @Override
    public String unlockEffect(Actor actor, Location location) {
        actor.heal(5);
        return actor + " unlocked the Titanium Door — decontamination sequence heals 5 HP!";
    }
}
