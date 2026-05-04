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
import game.capabilities.Sellable;
import game.grounds.Fire;

import java.util.Random;

/**
 * A massive, incredibly heavy piece of archaic junk, taking up 30 units
 * of weight in the worker's inventory.
 *
 * @author Jax
 */
public class CRTMonitor extends Item implements Sellable {
    private static final int HEALTH_ON_SELL = 5;
    private static final int DAMAGE_ON_SHORT = 2;
    private static final int SHORT_PERCENTAGE_CHANCE = 20;

    private final Random random = new Random();

    /**
     * Constructor.
     */
    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(30));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(25));
        this.makePortable();
    }

    @Override
    public String sell(Actor actor, GameMap map) {
        actor.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.INCREASE, getStatistic(ItemStatistics.SELL_VALUE));
        actor.getInventory().remove(this);

        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.INCREASE, HEALTH_ON_SELL);

        if (random.nextInt(100) < SHORT_PERCENTAGE_CHANCE) {
            actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, DAMAGE_ON_SHORT);

            // Set surroundings on fire
            for (Exit exit : map.locationOf(actor).getExits()) {
                Location location = exit.getDestination();
                Ground ground = location.getGround();

                if (!(ground instanceof Fire)) {
                    location.setGround(new Fire(ground));
                }
            }
        }

        return this + " was sold for " + getStatistic(ItemStatistics.SELL_VALUE) + " credits";
    }
}
