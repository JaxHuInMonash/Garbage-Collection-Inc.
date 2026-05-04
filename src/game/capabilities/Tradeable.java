package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Tradeable {
    int getCreditValue();
    String soldBy(Actor actor, GameMap map);
}
