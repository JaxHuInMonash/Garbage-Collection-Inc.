package game.weapons;


import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

public class WorkerFist extends IntrinsicWeapon {
    private static final int DAMAGE = 2;
    private static final int HIT_RATE = 50;

    public WorkerFist() {
        super(DAMAGE, "punches", HIT_RATE, "fist");
    }
}
