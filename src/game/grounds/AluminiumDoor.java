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
 */
public class AluminiumDoor extends SecuredDoor {
    /**
     * Constructor.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door");
    }
    /**
     * Returns the minimum clearance level required: 1.
     * @return 1
     */
    @Override
    protected int requiredClearance() { return 1; }
    /**
     * Deals exactly 2 points of damage to the actor due to electrical short-circuit.
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of the unlock effect
     */
    @Override
    public String unlockEffect(Actor actor, Location location) {
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 2);
        return actor + " unlocked the Aluminium Door — electrical short-circuit deals 2 damage!";
    }
}