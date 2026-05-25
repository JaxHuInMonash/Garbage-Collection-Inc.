package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A small rectangular piece of plastic that holds entirely too much power
 * over your ability to walk through doors. Its primary function is to beep
 * happily when the player has clearance, and beep angrily when they don't.
 *
 * @author Tommy
 */
public class AccessCardLevel1 extends AccessCard {

    private static final int CLEARANCE = 1;
    private static final int WEIGHT = 1;
    private static final int BUY_VALUE = 50;

    /**
     * Constructor.
     */
    public AccessCardLevel1() {
        super("Access Card (Level 1)", '▤', CLEARANCE, WEIGHT, BUY_VALUE);
    }

    /**
     * Defines what happens when a Level 1 card is bought.
     * Adds the card to the actor's inventory.
     *
     * @param actor the actor buying the card
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onBought(Actor actor, GameMap map) {
        actor.getInventory().add(this);
        return actor + " bought " + this + " for " + getBuyValue() + " credits.";
    }
}
