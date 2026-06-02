package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;

public class ZoneTrespassTrigger implements InfractionTrigger {
    private final String zoneName;

    public ZoneTrespassTrigger(String zoneName) { this.zoneName = zoneName; }

    @Override
    public void checkAndApply(Actor violator, GameMap map) {
        if (!(violator instanceof ContractedWorker)) return;
        System.out.println("[INFRACTION] " + violator + " trespassed into: " + zoneName);
        InfractionManager.getInstance().increment(violator, map);
    }
}