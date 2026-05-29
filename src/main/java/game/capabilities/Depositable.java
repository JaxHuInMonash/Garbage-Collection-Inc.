package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Depositable {
    String onDeposited(Actor actor, GameMap map);
    int getDepositValue();
}

