package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;
import game.items.AlienCube;
import game.capabilities.Ability;
import game.penalty.SabotageTrigger;

/**
 * An action that uses an {@link AlienCube} to teleport the actor to a
 * specific destination within the current map.
 * <p>
 * The spec requires the cube to offer <em>three</em> random destinations to
 * choose from — {@link AlienCube#allowableActions(Actor, GameMap)} therefore
 * creates three of these actions per turn, each preselected with a different
 * candidate {@link Location}. The player picks one from the menu.
 * <p>
 * After teleportation, all adjacent ground tiles at the source location are
 * permanently corrupted into {@link ToxicWaste}, which deals 1 damage per
 * turn to any actor standing on it.
 *
 * @author zhengjun Yang
 * @author Jax
 */
public class UseAlienCubeAction extends Action {

    private final AlienCube cube;
    private final Location target;

    /**
     * Constructor.
     *
     * @param cube   the AlienCube being used
     * @param target the pre-selected destination on the current map
     */
    public UseAlienCubeAction(AlienCube cube, Location target) {
        this.cube = cube;
        this.target = target;
    }

    /**
     * Teleports the actor to the chosen destination and corrupts the source
     * location's adjacent tiles into ToxicWaste.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is currently on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location source = map.locationOf(actor);

        corruptSurroundings(source);
        new SabotageTrigger().checkAndApply(actor, map);
        map.moveActor(actor, target);
        actor.getInventory().remove(cube);
        return actor + " uses the Alien Cube! Source corrupted into Toxic Waste. Teleported to ("
                + target.x() + ", " + target.y() + ")!";
    }

    /**
     * Returns a description of this action for the menu, including the
     * destination coordinates so the player can distinguish the three options.
     *
     * @param actor the actor performing the action
     * @return a menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses Alien Cube → teleport to (" + target.x() + ", " + target.y() + ")";
    }

    /**
     * Turns all adjacent non-wall, non-toxic ground tiles at the given
     * location into ToxicWaste.
     *
     * @param location the source location to corrupt around
     */
    private void corruptSurroundings(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!ground.hasAbility(Ability.BLOCKS_ENTRY) && !ground.hasAbility(Ability.IS_TOXIC)) {
                adjacent.setGround(new ToxicWaste());
            }
        }
    }
}
