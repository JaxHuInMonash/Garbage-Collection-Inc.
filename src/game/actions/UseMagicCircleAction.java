package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.MagicCircle;
import game.grounds.Wall;
import game.items.Flask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UseMagicCircleAction extends Action {

    private final Location circleLocation;
    private final String direction;
    private static final Random random = new Random();

    public UseMagicCircleAction(Location circleLocation, String direction) {
        this.circleLocation = circleLocation;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        List<Location> otherCircles = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location loc = map.at(x, y);
                if (loc.getGround() instanceof MagicCircle && !loc.equals(circleLocation)) {
                    otherCircles.add(loc);
                }
            }
        }

        if (otherCircles.isEmpty()) {
            return actor + " steps onto the Magic Circle, but there are no other circles to travel to...";
        }

        Location destination = otherCircles.get(random.nextInt(otherCircles.size()));
        map.moveActor(actor, destination);
        spawnFlaskAdjacent(destination);

        return actor + " teleports via Magic Circle to (" + destination.x() + ", " + destination.y()
                + ")! A Flask materialises nearby!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " steps through Magic Circle (" + direction + ")";
    }

    private void spawnFlaskAdjacent(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!(adjacent.getGround() instanceof Wall) && !adjacent.containsAnActor()
                    && adjacent.getItems().isEmpty()) {
                adjacent.addItem(new Flask());
                return;
            }
        }
    }
}
