package game.items;

import edu.monash.fit2099.engine.actors.Actor;
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
import game.statuses.BurnStatus;

import java.util.Random;

/**
 * An unstable light source weighing 7 units. Starts with 10 units of oil fuel.
 * <p>
 * While being carried, it has a 5% chance each turn to leak and ignite the
 * ground beneath the actor, spawning a {@link Fire} on that tile and consuming
 * 1 unit of oil fuel.
 *
 * @author Jax
 */
public class Lantern extends Item implements Sellable {
    private static final int WEIGHT = 7;

    private static final int BURN_ON_SELL_PERCENTAGE_CHANCE = 50;
    private static final int EXPLODE_ON_SELL_PERCENTAGE_CHANCE = 25;
    private static final int LEAK_CHANCE_PERCENT = 5;

    private static final int INITIAL_OIL_QUANTITY = 10;
    private static final int VALUE_PER_OIL = 5;
    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 5;

    private final Random random;

    /**
     * Constructor.
     */
    public Lantern() {
        super("Lantern", '&');
        this.random = new Random();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.QUANTITY, new BaseStatistic(INITIAL_OIL_QUANTITY)); // Oil Fuel
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(getStatistic(ItemStatistics.QUANTITY) * VALUE_PER_OIL));
        this.makePortable();
    }

    @Override
    public String sell(Actor actor, GameMap map) {
        actor.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.INCREASE, getStatistic(ItemStatistics.SELL_VALUE));
        actor.getInventory().remove(this);

        if (random.nextInt(100) < BURN_ON_SELL_PERCENTAGE_CHANCE) {
            actor.addStatus(new BurnStatus(BURN_DAMAGE, BURN_DURATION));
        }

        if (random.nextInt(100) < EXPLODE_ON_SELL_PERCENTAGE_CHANCE) {
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

    /**
     * Returns a string representation including remaining oil fuel,
     * since the oil level is meaningful gameplay state for the player.
     *
     * @return the lantern's name and oil level
     */
    @Override
    public String toString() {
        return "Lantern (oil: " + getStatistic(ItemStatistics.QUANTITY) + "/" + getMaximumStatistic(ItemStatistics.QUANTITY) + ")";
    }

    /**
     * Each turn while carried, the lantern has a 5% chance to leak.
     * If it leaks, it consumes 1 unit of oil and spawns a Fire on the
     * ground beneath the actor carrying it.
     *
     * @param currentLocation the location of the actor carrying this lantern
     * @param actor           the actor carrying this lantern
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (getStatistic(ItemStatistics.QUANTITY) > 0 && random.nextInt(100) < LEAK_CHANCE_PERCENT) {
            modifyStatistic(ItemStatistics.QUANTITY, StatisticOperations.DECREASE, 1);
            modifyStatistic(ItemStatistics.SELL_VALUE, StatisticOperations.UPDATE, getStatistic(ItemStatistics.QUANTITY) * VALUE_PER_OIL);
            Ground currentGround = currentLocation.getGround();
            // Only spawn fire if the ground isn't already on fire
            if (!(currentGround instanceof Fire)) {
                currentLocation.setGround(new Fire(currentGround));
            }
        }
    }
}
