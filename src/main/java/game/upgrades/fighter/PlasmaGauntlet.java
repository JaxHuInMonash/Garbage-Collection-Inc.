package game.upgrades.fighter;

import edu.monash.fit2099.engine.actors.Actor;
import game.capabilities.IntrinsicWeaponEquippable;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;
import game.weapons.PlasmaGauntletWeapon;

/**
 * Combat path, Tier 3 — <strong>Plasma Gauntlet</strong>.
 * <p>
 * <strong>On apply (one-shot):</strong> replaces the worker's existing
 * intrinsic weapon (their default {@link game.weapons.WorkerFist}) with a
 * {@link PlasmaGauntletWeapon}. This is a <em>weapon upgrade</em>, not an
 * attack unlock — workers can already attack hostiles bare-fisted from turn 1
 * (used by REQ4's friendly-fire mechanic); the gauntlet just makes that
 * pathway considerably more dangerous. The weapon itself encapsulates every
 * per-attack effect (tile ignition, on-kill AoE burn) so this upgrade class
 * stays trivially thin — all complexity lives where the cross-component
 * interaction happens.
 * <p>
 * The weapon swap is performed through the
 * {@link IntrinsicWeaponEquippable} capability (discovered via
 * {@link Actor#asCapability(Class)}), keeping this upgrade decoupled from
 * the concrete worker class. If the receiving actor doesn't expose the
 * capability (i.e. isn't a worker), the install is silently skipped — the
 * Lab Station's eligibility check should prevent that case in practice.
 *
 * @author Jax
 */
public class PlasmaGauntlet implements Upgrade {

    private static final int TIER = 3;
    private static final int WORKER_COST = 300;
    private static final int COMPANY_COST = 250;

    @Override public String getName()              { return "Plasma Gauntlet"; }
    @Override public UpgradePath getPath()         { return UpgradePath.FIGHTER; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        worker.asCapability(IntrinsicWeaponEquippable.class)
              .ifPresent(equip -> equip.equipIntrinsicWeapon(new PlasmaGauntletWeapon()));
    }
}
