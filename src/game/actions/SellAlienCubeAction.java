package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.AlienCube;

public class SellAlienCubeAction extends Action {

    private final AlienCube cube;

    public SellAlienCubeAction(AlienCube cube) {
        this.cube = cube;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return cube.soldBy(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells Alien Cube to Supercomputer (25 credits, spawns Undead!)";
    }
}
