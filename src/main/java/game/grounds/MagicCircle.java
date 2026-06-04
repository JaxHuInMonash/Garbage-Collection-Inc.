package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UseMagicCircleAction;
import game.capabilities.Ability;
import game.display.EventLog;
import game.statuses.PhaseStabilizerStatus;

/**
 * A magical marking found inside 20-overflow. Displays as {@code ◎}.
 * <p>
 * Magic Circles allow within-map teleportation. Using one randomly teleports
 * the worker to one of the other magic circles on the map. The magical energy
 * surges upon arrival, instantly spawning a Flask on an empty tile directly
 * next to the destination circle. The action is only offered while the worker
 * is standing on the circle (not when merely adjacent).
 * <p>
 * <strong>Surveyor T3 interaction:</strong> if a phase-stabilised worker (one
 * with {@link PhaseStabilizerStatus}) stands on this circle, the circle
 * <em>despawns</em> instead of being usable — see {@link #tick(Location)}.
 *
 * @author zhengjun Yang
 * @author Jax
 */
public class MagicCircle extends Ground {

    /**
     * Constructor.
     */
    public MagicCircle() {
        super('◎', "Magic Circle");
        this.enableAbility(Ability.IS_MAGIC_CIRCLE);
    }

    /**
     * Offers the teleport action when the actor is standing on this tile, but
     * <strong>not</strong> when the worker is phase-stabilised — they cause the
     * circle to despawn rather than triggering it.
     *
     * @param actor     the Actor standing on or near this circle
     * @param location  the location of this circle
     * @param direction the direction of the circle from the actor ("" if standing on it)
     * @return a list containing the use action if standing on the circle
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty() && !actor.hasStatus(PhaseStabilizerStatus.class)) {
            actions.add(new UseMagicCircleAction(location));
        }
        return actions;
    }

    /**
     * If a phase-stabilised worker is standing on this circle, replace the
     * circle with plain {@link Floor} — the circle's space-warping field cannot
     * lock on to a stabilised target.
     *
     * @param location the location of this circle
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()
                && location.getActor().hasStatus(PhaseStabilizerStatus.class)) {
            Actor worker = location.getActor();
            location.setGround(new Floor());
            EventLog.getInstance().log(
                    worker + " phases through the Magic Circle — its pattern collapses into floor.");
        }
    }
}
