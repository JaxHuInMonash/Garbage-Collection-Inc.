package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A behaviour that makes an actor move to a random valid adjacent location.
 * <p>
 * Each turn, the behaviour collects all exits that the actor can enter
 * and picks one at random. If no valid exit exists, returns null (the
 * actor stays put).
 *
 * @author Jax
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {

    private final Random random = new Random();

    /**
     * Returns a MoveActorAction to a random valid adjacent location,
     * or null if no movement is possible.
     *
     * @param actor    the actor that will wander
     * @param location the current location of the actor
     * @return a MoveActorAction to a random adjacent location, or null
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Exit> validExits = new ArrayList<>();

        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                validExits.add(exit);
            }
        }

        if (validExits.isEmpty()) {
            return null;
        }

        Exit chosen = validExits.get(random.nextInt(validExits.size()));
        return chosen.getDestination().getMoveAction(actor, chosen.getName(), chosen.getHotKey());
    }
}
