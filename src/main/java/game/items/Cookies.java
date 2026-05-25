package game.items;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.actors.Parasite;
import game.capabilities.Ability;
import game.capabilities.Consumable;
import game.capabilities.Infectable;
import game.spawning.SpawnEffectManager;
import game.statuses.InfectedStatus;
import game.capabilities.Sellable;

/**
 * A packet of cookies found on the moon's facility. Weighs 2 units and
 * contains 5 individual cookies (i.e., can be used five times).
 * <p>
 * Without a {@link SterilisationBox}, eating one permanently decreases the
 * consumer's maximum HP by 1. With sterilisation, each cookie heals 1 HP.
 * The item disappears from the inventory only after all 5 cookies are eaten.
 * <p>
 * The remaining cookie count is the single source of truth, exposed via the
 * {@link ItemStatistics#QUANTITY} statistic. Sell value is computed on demand
 * from the current quantity, so the two never drift out of sync.
 *
 * @author Jax
 * @author Tommy
 */
public class Cookies extends Item implements Consumable, Sellable, Infectable {
    private static final int WEIGHT = 2;

    private static final int INITIAL_QUANTITY = 5;
    private static final int VALUE_PER_COOKIE = 1;

    private static final int SELL_DAMAGE_PER_COOKIE = 1;
    private static final int CONSUME_STERILISED_HEAL = 1;
    private static final int CONSUME_UNSTERILISED_MAX_HP_LOSS = 1;

    /**
     * Constructor.
     */
    public Cookies() {
        super("Cookies", '◍');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.QUANTITY, new BaseStatistic(INITIAL_QUANTITY));
        this.makePortable();
    }

    /**
     * Returns the number of cookies remaining.
     *
     * @return remaining cookie count
     */
    public int getRemainingCookies() {
        return getStatistic(ItemStatistics.QUANTITY);
    }

    /**
     * Defines what happens when a cookie is sold.
     * Removes the Cookies from the actor's inventory
     * and damages them by {@value SELL_DAMAGE_PER_COOKIE} per cookie
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onSold(Actor actor, GameMap map) {
        int quantity = getStatistic(ItemStatistics.QUANTITY);
        int sellValue = getSellValue();
        actor.getInventory().remove(this);

        actor.hurt(SELL_DAMAGE_PER_COOKIE * quantity);

        return this + " were sold for " + sellValue + " credits, harming the player " + SELL_DAMAGE_PER_COOKIE + " for each cookie.";
    }

    /**
     * Sell value is dynamic: 1 credit per remaining cookie.
     *
     * @return the current sell value
     */
    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.QUANTITY) * VALUE_PER_COOKIE;
    }

    /**
     * Called when these cookies are infected by a Parasite. Adds the generic
     * {@link InfectedStatus} which delegates per-turn logic back to this class.
     *
     * @param infector the Parasite that caused the infection
     * @param map      the current game map
     */
    @Override
    public void onInfected(Actor infector, GameMap map) {
        this.addStatus(new InfectedStatus(this));
    }

    /**
     * Per-turn infection logic: reduces cookies by 1 and spawns a Parasite
     * on an adjacent empty tile each turn.
     *
     * @param location the current location of these cookies
     */
    @Override
    public void onInfectionTick(Location location) {
        if (getStatistic(ItemStatistics.QUANTITY) > 0) {
            modifyStatistic(ItemStatistics.QUANTITY, StatisticOperations.DECREASE, 1);
        }
        spawnParasiteAdjacent(location);
    }

    /**
     * The infection remains active while cookies remain.
     *
     * @return true if there are cookies remaining
     */
    @Override
    public boolean isInfectionActive() {
        return getStatistic(ItemStatistics.QUANTITY) > 0;
    }

    /**
     * Attempts to spawn a Parasite on an adjacent empty tile.
     *
     * @param location the location around which to spawn
     */
    private void spawnParasiteAdjacent(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Parasite parasite = new Parasite();
            if (adjacent.canActorEnter(parasite)) {
                try {
                    adjacent.addActor(parasite);
                    SpawnEffectManager.getInstance().onCreatureSpawned(parasite, adjacent);
                } catch (GameEngineException e) {
                    // Tile became occupied between check and placement — skip
                }
                return;
            }
        }
    }

    /**
     * Defines what happens when a cookie is consumed. With sterilisation, heals
     * 1 HP. Without, permanently decreases the consumer's maximum HP by 1.
     * Removes the item from the inventory once all cookies are eaten.
     *
     * @param actor the actor consuming a cookie
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        modifyStatistic(ItemStatistics.QUANTITY, StatisticOperations.DECREASE, 1);

        String result;
        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            actor.heal(CONSUME_STERILISED_HEAL);
            result = actor + " eats a sterilised cookie and heals " + CONSUME_STERILISED_HEAL + " HP.";
        } else {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, CONSUME_UNSTERILISED_MAX_HP_LOSS);
            result = actor + " eats a toxic cookie. Maximum HP permanently decreased by " + CONSUME_UNSTERILISED_MAX_HP_LOSS + "!";
        }

        if (getStatistic(ItemStatistics.QUANTITY) <= 0) {
            actor.getInventory().remove(this);
            result += " The cookie packet is now empty and discarded.";
        } else {
            result += " (" + getStatistic(ItemStatistics.QUANTITY) + " cookies remaining)";
        }

        return result;
    }

    @Override
    public void removeFromGround(Location location) {
        location.removeItem(this);
    }

    @Override
    public String getConsumeMenuDescription(Actor actor) {
        return actor + " eats a cookie (" + getStatistic(ItemStatistics.QUANTITY) + " remaining)";
    }

    /**
     * Provides the correct name when displaying the name
     * @return Singular or plural depending on quantity
     */
    @Override
    public String toString() {
        return "Cookie" + (getStatistic(ItemStatistics.QUANTITY) == 1 ? "" : "s");
    }

    /**
     * Offers the consume action while cookies remain.
     *
     * @param owner the actor carrying these cookies
     * @param map   the current game map
     * @return a list containing the ConsumeAction if cookies remain
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (getStatistic(ItemStatistics.QUANTITY) > 0) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}
