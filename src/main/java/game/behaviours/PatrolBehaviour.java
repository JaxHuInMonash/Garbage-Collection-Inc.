package game.behaviours;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatrolBehaviour extends EnforcerBehaviour {
    private final Random random = new Random();

    public PatrolBehaviour(Actor target) { super(target); }

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
