package game.behaviours;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Extends {@link EnforcerBehaviour}.
 * <p>
 * When the {@link game.actors.EnforcerBot} has no immediate chase or despawn
 * action to perform, it falls back to this behaviour and wanders the map
 * randomly each turn, keeping it visible as a deterrent.
 *
 * @author Zhengjun Yang
 */
public class PatrolBehaviour extends EnforcerBehaviour {
    private final Random random = new Random();
    /**
     * Constructor.
     *
     * @param target the Worker this bot is associated with
     */
    public PatrolBehaviour(Actor target) { super(target); }
    /**
     * Moves the bot to a random adjacent empty tile each turn.
     *
     * @param bot      the EnforcerBot actor
     * @param location the bot's current location
     * @return a random move action, or null if no valid moves exist
     */
    @Override
    public Action operate(Actor bot, Location location) {
        List<Exit> validExits = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(bot) && !dest.containsAnActor())
                validExits.add(exit);
        }
        if (validExits.isEmpty()) return null;
        Exit chosen = validExits.get(random.nextInt(validExits.size()));
        return chosen.getDestination().getMoveAction(bot, chosen.getName(), chosen.getHotKey());
    }
}
