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
    private static final int REQUIRED_CLEARANCE = 3;
    private static final int UNLOCK_EFFECT_HEAL = 5;
    /**
     * Constructor.
     */
    public TitaniumDoor() {
        super('M', "Titanium Door", REQUIRED_CLEARANCE);
    }

    /**
     * Heals the actor for {@value UNLOCK_EFFECT_HEAL} HP via decontamination sequence.
     *
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of the unlock effect
     */
    @Override
    public String unlockEffect(Actor actor, Location location) {
        actor.heal(UNLOCK_EFFECT_HEAL);
        return actor + " unlocked the Titanium Door — decontamination sequence heals " + UNLOCK_EFFECT_HEAL + " HP!";
    }
}
