package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Cuttable;
import game.items.AluminiumScrap;

import java.util.Random;

/**
 * An aluminium door requiring Clearance Level 1 or higher to open.
 * Displays as =.
 * Unlocking this door causes a faulty electrical short-circuit,
 * dealing exactly 2 points of damage to the actor who unlocks it.
 * In the 99-deprecated map, all doors are Aluminium Doors.
 * <p>
 * Can also be cut open by a Plasma Cutter (REQ1): drops Aluminium Scrap,
 * transforms into a Floor, and has a 25% chance of exploding.
 *
 * @author zhengjun Yang
 * @author Tommy
 * @author Mary
 */
public class AluminiumDoor extends SecuredDoor implements Cuttable {

    private static final int REQUIRED_CLEARANCE  = 1;
    private static final int UNLOCK_EFFECT_DAMAGE = 2;
    private static final double EXPLOSION_CHANCE  = 0.25;
    private static final int EXPLOSION_DAMAGE     = 100;

    private static final Random RANDOM = new Random();

    /**
     * Constructor.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door", REQUIRED_CLEARANCE);
    }

    /**
     * Deals exactly {@value UNLOCK_EFFECT_DAMAGE} points of damage to the
     * actor due to electrical short-circuit.
     *
     * @param actor    the actor unlocking the door
     * @param location the location of this door
     * @return a description of the unlock effect
     */
    @Override
    public String unlockEffect(Actor actor, Location location) {
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, UNLOCK_EFFECT_DAMAGE);
        return actor + " unlocked the Aluminium Door — electrical short-circuit deals "
                + UNLOCK_EFFECT_DAMAGE + " damage!";
    }

    /**
     * Cuts the door open with a Plasma Cutter:
     * <ol>
     *   <li>Drops Aluminium Scrap on the tile.</li>
     *   <li>Transforms this tile into a Floor.</li>
     *   <li>25% chance of explosion — deals 100 damage to adjacent entities.</li>
     * </ol>
     *
     * @param actor          the actor performing the cut
     * @param targetLocation the location of this door
     * @param map            the current game map
     * @return a description of what happened
     */
    @Override
    public String onCut(Actor actor, Location targetLocation, GameMap map) {
        targetLocation.addItem(new AluminiumScrap());
        targetLocation.setGround(new Floor());

        StringBuilder sb = new StringBuilder();
        sb.append(actor).append(" cuts open the Aluminium Door — Aluminium Scrap falls to the floor.");

        if (RANDOM.nextDouble() < EXPLOSION_CHANCE) {
            sb.append(" The door EXPLODES!");
            for (Exit exit : targetLocation.getExits()) {
                Location adjacent = exit.getDestination();
                if (adjacent.containsAnActor()) {
                    adjacent.getActor().hurt(EXPLOSION_DAMAGE);
                    sb.append(" ").append(adjacent.getActor())
                            .append(" takes ").append(EXPLOSION_DAMAGE).append(" explosion damage!");
                }
            }
        }

        String msg = sb.toString();
        System.out.println(msg);
        return msg;
    }
}