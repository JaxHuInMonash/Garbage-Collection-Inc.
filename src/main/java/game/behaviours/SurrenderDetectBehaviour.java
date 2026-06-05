package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ChaseAndFineAction;
/**
 * Extends {@link EnforcerBehaviour}.
 * <p>
 * Each turn, checks whether the target Worker is still present and conscious.
 * If the target has left the map or become unconscious, the
 * {@link game.actors.EnforcerBot} despawns automatically via
 * {@link ChaseAndFineAction.DespawnAction}.
 *
 * @author Zhengjun Yang
 */
public class SurrenderDetectBehaviour extends EnforcerBehaviour {
    /**
     * Constructor.
     *
     * @param target the Worker being monitored
     */
    public SurrenderDetectBehaviour(Actor target) { super(target); }
    /**
     * Returns a despawn action if the target is gone or unconscious,
     * otherwise returns null to allow other behaviours to proceed.
     *
     * @param bot      the EnforcerBot actor
     * @param location the bot's current location
     * @return a DespawnAction if target is gone, null otherwise
     */
    @Override
    public Action operate(Actor bot, Location location) {
        GameMap map = location.map();
        if (!map.contains(target) || !target.isConscious())
            return new ChaseAndFineAction.DespawnAction(bot, target);
        return null;
    }
}
