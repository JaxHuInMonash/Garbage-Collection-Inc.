package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.capabilities.Ability;
import game.capabilities.Sellable;
import game.grounds.Fire;

import java.util.Random;

/**
 * A massive, incredibly heavy piece of archaic junk, taking up 30 units
 * of weight in the worker's inventory.
 *
 * @author Jax
 * @author Tommy
 */
public class CRTMonitor extends Item implements Sellable {
    private static final int WEIGHT = 30;
    private static final int SELL_VALUE = 25;

    private static final int HEALTH_ON_SELL = 5;
    private static final int DAMAGE_ON_SHORT = 2;
    private static final int SHORT_PERCENTAGE_CHANCE = 20;

    private final Random random = new Random();

    /**
     * Constructor.
     */
    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    /**
     * Defines what happens when an CRT Monitor is sold.
     * Removes the CRT Monitor from the actor's inventory.
     * There is an additional chance of {@value SHORT_PERCENTAGE_CHANCE}
     * for the CRT Monitor to short out and damage the actor
     * {@value DAMAGE_ON_SHORT} damage and surround the CRT
     * Monitor with fire
     *
     * @param actor the actor selling the CRT Monitor
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        actor.heal(HEALTH_ON_SELL);

        if (random.nextInt(100) < SHORT_PERCENTAGE_CHANCE) {
            actor.hurt(DAMAGE_ON_SHORT);

            for (Exit exit : map.locationOf(actor).getExits()) {
                Location location = exit.getDestination();
                Ground ground = location.getGround();

                if (ground.hasAbility(Ability.ON_FIRE)) { // Dysfunctional in REQ1, Functional when merged
                    location.setGround(new Fire(ground));
                }
            }
        }

        return this + " was sold for " + getStatistic(ItemStatistics.SELL_VALUE) + " credits";
    }

    /**
     * Provides the value of the item when sold
     * @return current sell value of the item
     */
    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }
}
