package game.penalty;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;

public class SabotageTrigger implements InfractionTrigger {

    @Override
    public void checkAndApply(Actor violator, GameMap map) {
        if (!(violator instanceof ContractedWorker)) return;
        System.out.println("[INFRACTION] " + violator + " sabotaged the facility!");
        InfractionManager.getInstance().increment(violator, map);
    }
}
