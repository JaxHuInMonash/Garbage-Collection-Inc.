package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Lantern;

/**
 * An infection status applied to a {@link Lantern} by a Parasite.
 * <p>
 * Each turn, the infected lantern loses 1 unit of oil fuel. The infection
 * remains active as long as the lantern has oil remaining.
 *
 * @author Jax
 */
public class LanternInfectedStatus implements Status {

    private final Lantern lantern;

    /**
     * Constructor.
     *
     * @param lantern the lantern being infected
     */
    public LanternInfectedStatus(Lantern lantern) {
        this.lantern = lantern;
    }

    /**
     * Each tick, drain 1 unit of oil from the lantern.
     *
     * @param currEntity the infected lantern (same as the stored reference)
     * @param location   the location of the lantern
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        lantern.drainOil(1);
    }

    /**
     * The infection remains active while the lantern still has oil.
     *
     * @return true if the lantern has oil remaining
     */
    @Override
    public boolean isStatusActive() {
        return lantern.getOilFuel() > 0;
    }
}
