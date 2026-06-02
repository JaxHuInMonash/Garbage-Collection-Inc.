package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;

public class EquipmentTheftTrigger implements InfractionTrigger {
    private final Actor owner;

    public EquipmentTheftTrigger(Actor owner) { this.owner = owner; }

    @Override
    public void checkAndApply(Actor violator, GameMap map) {
        if (!(violator instanceof ContractedWorker)) return;
        if (violator == owner) return;
        System.out.println("[INFRACTION] " + violator + " stole equipment from " + owner);
        InfractionManager.getInstance().increment(violator, map);
    }

    public static void checkTheft(Actor picker, Actor owner, GameMap map) {
        if (picker != owner && picker instanceof ContractedWorker) {
            new EquipmentTheftTrigger(owner).checkAndApply(picker, map);
        }
    }
}
