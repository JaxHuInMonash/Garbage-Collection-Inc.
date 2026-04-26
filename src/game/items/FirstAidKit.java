package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.UseFirstAidKitAction;

/**
 * A super useful first aid kit weighing 25 units. It can be used to permanently
 * increase a worker's maximum health by 1 point while simultaneously restoring
 * their health to full.
 * <p>
 * It requires a 20-turn cooldown between uses. Crucially, the cooldown timer
 * only progresses while the kit is actively being carried by a worker; if it is
 * left on the floor, the cooldown timer stops.
 *
 * @author Jax
 */
public class FirstAidKit extends Item {

    private static final int COOLDOWN_TURNS = 20;
    private int cooldownRemaining;

    /**
     * Constructor.
     * Creates a FirstAidKit with weight 25, made portable, and ready to use immediately.
     */
    public FirstAidKit() {
        super("First Aid Kit", '+');
        this.cooldownRemaining = 0;
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(25));
        this.makePortable();
    }

    /**
     * Checks whether the kit is ready to use (cooldown has expired).
     *
     * @return true if the kit can be used, false if on cooldown
     */
    public boolean isReady() {
        return cooldownRemaining <= 0;
    }

    /**
     * Returns the number of turns remaining on the cooldown.
     *
     * @return the cooldown turns remaining
     */
    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    /**
     * Starts the cooldown timer after the kit has been used.
     */
    public void startCooldown() {
        this.cooldownRemaining = COOLDOWN_TURNS;
    }

    /**
     * Ticks the cooldown timer while being carried by an actor.
     * The timer only progresses while the kit is in a worker's inventory.
     *
     * @param currentLocation the location of the actor carrying this item
     * @param actor           the actor carrying this item
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
    }

    /**
     * Provides the use action to the owner when the kit is off cooldown.
     *
     * @param owner the actor carrying this kit
     * @param map   the map where the actor is
     * @return a list containing the UseFirstAidKitAction if ready, empty otherwise
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (isReady()) {
            actions.add(new UseFirstAidKitAction(this));
        }
        return actions;
    }
}
