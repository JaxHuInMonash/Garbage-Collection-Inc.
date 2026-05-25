package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A Level 2 access card. Weighs 2 units and represented by {@code α}.
 * <p>
 * To "calibrate" the card to the user, the Supercomputer forcefully extracts
 * a blood sample, dealing exactly {@value DAMAGE_ON_BUY} points of damage to
 * the buyer immediately on purchase.
 *
 * @author Tommy
 */
public class AccessCardLevel2 extends AccessCard {

    private static final int CLEARANCE = 2;
    private static final int WEIGHT = 2;
    private static final int BUY_VALUE = 100;
    private static final int DAMAGE_ON_BUY = 5;

    /**
     * Constructor.
     */
    public AccessCardLevel2() {
        super("Access Card (Level 2)", 'α', CLEARANCE, WEIGHT, BUY_VALUE);
    }

    /**
     * Places this card into the buyer's inventory. The polymorphic
     * {@code Inventory.add()} handles any weight check.
     */
    @Override
    public boolean attemptAcquireBy(Actor actor) {
        return actor.getInventory().add(this);
    }

    /**
     * Side effects after a Level 2 card is successfully acquired:
     * damages the buyer by {@value DAMAGE_ON_BUY} (calibration blood sample).
     *
     * @param actor the actor buying the card
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onBought(Actor actor, GameMap map) {
        actor.hurt(DAMAGE_ON_BUY);
        return actor + " bought " + this + " for " + getBuyValue() + " credits — blood sample extracted (" + DAMAGE_ON_BUY + " damage).";
    }
}
