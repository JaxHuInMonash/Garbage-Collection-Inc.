package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
/**
 * An aluminium door requiring Clearance Level 1 or higher to open.
 * Displays as =.
 * Unlocking this door causes a faulty electrical short-circuit,
 * dealing exactly 2 points of damage to the actor who unlocks it.
 * In the 99-deprecated map, all doors are Aluminium Doors.
 * @author zhengjun Yang
 * @author Tommy
 */
public class AluminiumDoor extends SecuredDoor {
    private static final int REQUIRED_CLEARANCE = 1;

    private static final int UNLOCK_EFFECT_DAMAGE = 2;

    /**
     * Constructor.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door", REQUIRED_CLEARANCE);
    }

    /**
     * Deals exactly {@value UNLOCK_EFFECT_DAMAGE} points of damage to the actor due to electrical short-circuit.
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of the unlock effect
     */
    @Override
    public String unlockEffect(Actor actor, Location location) {
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, UNLOCK_EFFECT_DAMAGE);
        return actor + " unlocked the Aluminium Door — electrical short-circuit deals " + UNLOCK_EFFECT_DAMAGE + " damage!";
    }
}