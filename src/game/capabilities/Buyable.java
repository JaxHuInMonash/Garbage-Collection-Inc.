package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Buyable {
    default String onInsufficientCredits(Actor actor, GameMap map) {
        return ".";
    } // Used for FirstAidKit

    String buy(Actor actor, GameMap map);

    int getBuyValue();
}
