package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;
import game.grounds.Wall;
import game.items.AlienCube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UseAlienCubeAction extends Action {

    private final AlienCube cube;
    private final int destinationIndex;
    private static final Random random = new Random();

    private static List<Location> cachedDestinations = null;
    private static GameMap cachedMap = null;

    public UseAlienCubeAction(AlienCube cube, int destinationIndex) {
        this.cube = cube;
        this.destinationIndex = destinationIndex;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location source = map.locationOf(actor);
        List<Location> destinations = getDestinations(map);
        Location target = destinations.get(destinationIndex % destinations.size());

        corruptSurroundings(source);
        map.moveActor(actor, target);

        cachedDestinations = null;
        cachedMap = null;

        return actor + " uses the Alien Cube! Source corrupted into Toxic Waste. Teleported to ("
                + target.x() + ", " + target.y() + ")!";
    }

    @Override
    public String menuDescription(Actor actor) {
        List<Location> destinations = getDestinations(null);
        String loc = (destinations != null && destinationIndex < destinations.size())
                ? "(" + destinations.get(destinationIndex).x() + ", " + destinations.get(destinationIndex).y() + ")"
                : "random location " + (destinationIndex + 1);
        return actor + " uses Alien Cube → destination " + (destinationIndex + 1) + " " + loc;
    }

    private static List<Location> getDestinations(GameMap map) {
        if (map == null) return cachedDestinations;
        if (cachedMap == map && cachedDestinations != null) return cachedDestinations;

        List<Location> candidates = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location loc = map.at(x, y);
                if (!(loc.getGround() instanceof Wall)) {
                    candidates.add(loc);
                }
            }
        }
        Collections.shuffle(candidates, random);
        cachedDestinations = candidates.subList(0, Math.min(3, candidates.size()));
        cachedMap = map;
        return cachedDestinations;
    }

    private void corruptSurroundings(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!(ground instanceof Wall) && !(ground instanceof ToxicWaste)) {
                adjacent.setGround(new ToxicWaste());
            }
        }
    }
}
