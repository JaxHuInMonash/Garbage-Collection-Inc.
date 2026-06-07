package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.penalty.ZoneTrespassTrigger;
/**
 * A ground tile that marks an area as restricted.
 * <p>
 * Each turn, if a {@link game.actors.ContractedWorker} is standing on this tile,
 * the {@link ZoneTrespassTrigger} fires and increments their infraction level.
 * Restricted zones are placed during map initialisation in
 * {@link game.EclipseNebula}.
 *
 * @author Zhengjun Yang
 */
public class RestrictedGround extends Ground {
    private final ZoneTrespassTrigger trigger;
    /**
     * Constructor.
     *
     * @param zoneName the display name of this restricted zone
     */
    public RestrictedGround(String zoneName) {
        super('R', "Restricted Zone");
        this.trigger = new ZoneTrespassTrigger(zoneName);
    }
    /**
     * Each turn, fires the trigger if a Worker is standing on this tile.
     *
     * @param location the location of this ground tile
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            trigger.checkAndApply(actor, location.map());
        }
    }
}
