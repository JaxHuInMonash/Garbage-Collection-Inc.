package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;

public class FriendlyFireTrigger implements InfractionTrigger {

    @Override
    public void checkAndApply(Actor violator, GameMap map) {
        if (!(violator instanceof ContractedWorker)) return;
        System.out.println("[INFRACTION] " + violator + " committed friendly fire!");
        InfractionManager.getInstance().increment(violator, map);
    }

    public static void checkFriendlyFire(Actor attacker, Actor target, GameMap map) {
        if (attacker instanceof ContractedWorker && target instanceof ContractedWorker) {
            new FriendlyFireTrigger().checkAndApply(attacker, map);
        }
    }
}
