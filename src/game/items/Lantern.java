package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Ability;
import game.capabilities.Infectable;
import game.grounds.Fire;
import game.statuses.InfectedStatus;

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
public class Lantern extends Item implements Infectable {

    private static final int INITIAL_OIL = 10;
    private static final int LEAK_CHANCE_PERCENT = 5;

    private int oilFuel;
    private final Random random;

    /**
     * Constructor.
     */
    public Lantern() {
        super("Lantern", '&');
        this.oilFuel = INITIAL_OIL;
        this.random = new Random();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(7));
        this.makePortable();
    }

    /**
     * Called when this lantern is infected by a Parasite. Adds the generic
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
     * Per-turn infection logic: drains 1 unit of oil each turn.
     *
     * @param location the current location of this lantern
     */
    @Override
    public void onInfectionTick(Location location) {
        this.oilFuel = Math.max(0, this.oilFuel - 1);
    }

    /**
     * The infection remains active while the lantern still has oil.
     *
     * @return true if oil remains, false otherwise
     */
    @Override
    public boolean isInfectionActive() {
        return oilFuel > 0;
    }

    /**
     * Returns a string representation including remaining oil fuel,
     * since the oil level is meaningful gameplay state for the player.
     *
     * @return the lantern's name and oil level
     */
    @Override
    public String toString() {
        return "Lantern (oil: " + oilFuel + "/" + INITIAL_OIL + ")";
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
        if (oilFuel > 0 && random.nextInt(100) < LEAK_CHANCE_PERCENT) {
            oilFuel--;
            Ground currentGround = currentLocation.getGround();
            // Only spawn fire if the ground isn't already on fire
            if (!currentGround.hasAbility(Ability.ON_FIRE)) {
                currentLocation.setGround(new Fire(currentGround));
            }
        }
    }
}
