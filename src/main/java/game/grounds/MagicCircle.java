package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UseMagicCircleAction;
import game.actions.UseMagicCircleTargetedAction;
import game.capabilities.Ability;
import game.statuses.PhaseStabilizerStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * A magical marking found inside 20-overflow. Displays as {@code ◎}.
 * <p>
 * Magic Circles allow within-map teleportation. Using one randomly teleports
 * the worker to one of the other magic circles on the map. The magical energy
 * surges upon arrival, instantly spawning a Flask on an empty tile directly
 * next to the destination circle. The action is only offered while the worker
 * is standing on the circle (not when merely adjacent).
 * <p>
 * <ul>
 *    <li><strong>Regular worker</strong> — gets a single
 *         {@link UseMagicCircleAction} that teleports to a <em>random</em> other
 *         circle on the map (spawning a Flask adjacent on arrival).</li>
 *    <li><strong>Phase-stable worker (Surveyor T3)</strong> — gets one
 *         {@link UseMagicCircleTargetedAction} per other circle on the map, so
 *         the player can pick exactly where to land. The circle itself is
 *         preserved either way.</li>
 * </ul>
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
     * Offers teleportation actions only when the actor is standing on this
     * tile. Phase-stable workers get one action per candidate destination
     * circle (precise choice); regular workers get a single random-teleport
     * action.
     *
     * @param actor     the Actor standing on or near this circle
     * @param location  the location of this circle
     * @param direction the direction of the circle from the actor ("" if standing on it)
     * @return the eligible teleport actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!direction.isEmpty()) return actions;

        List<Location> otherCircles = findOtherCircles(location);
        if (otherCircles.isEmpty()) return actions;

        if (actor.hasStatus(PhaseStabilizerStatus.class)) {
            for (Location target : otherCircles) {
                actions.add(new UseMagicCircleTargetedAction(target));
            }
        } else {
            actions.add(new UseMagicCircleAction(location));
        }
        return actions;
    }

    /**
     * Scans the map for every other {@link Ability#IS_MAGIC_CIRCLE} tile,
     * excluding the one at {@code self}.
     */
    private List<Location> findOtherCircles(Location self) {
        GameMap map = self.map();
        List<Location> others = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location candidate = map.at(x, y);
                if (candidate.getGround().hasAbility(Ability.IS_MAGIC_CIRCLE)
                        && !candidate.equals(self)) {
                    others.add(candidate);
                }
            }
        }
        return others;
    }
}
