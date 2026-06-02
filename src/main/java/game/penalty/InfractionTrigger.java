package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface InfractionTrigger {
    void checkAndApply(Actor violator, GameMap map);
}