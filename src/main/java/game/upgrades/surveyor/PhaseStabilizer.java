package game.upgrades.surveyor;

import edu.monash.fit2099.engine.actors.Actor;
import game.statuses.PhaseStabilizerStatus;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

/**
 * Surveyor path, Tier 3 — <strong>Phase Stabilizer</strong>.
 * <p>
 * <strong>On apply (one-shot):</strong> installs a {@link PhaseStabilizerStatus}.
 * The status:
 * <ul>
 *   <li>holds a regenerating phase-charge (every {@value PhaseStabilizerStatus#RECHARGE_TURNS} turns);</li>
 *   <li>is consulted by {@link game.actors.ContractedWorker#playTurn} to offer a
 *       {@link game.actions.PhaseThroughWallAction} on each adjacent wall when charged
 *       (the wall is permanently converted to floor);</li>
 *   <li>is consulted by {@link game.grounds.WarperTreeMature#tick} and
 *       {@link game.grounds.MagicCircle#tick} so those tiles <em>despawn</em>
 *       instead of warping the phase-stable worker.</li>
 * </ul>
 * Cross-component: capability-marker × custom action × permanent terrain
 * modification × bypassing existing teleport mechanics with despawn semantics.
 *
 * @author Jax
 */
public class PhaseStabilizer implements Upgrade {

    private static final int TIER = 3;
    private static final int WORKER_COST = 300;
    private static final int COMPANY_COST = 250;

    @Override public String getName()              { return "Phase Stabilizer"; }
    @Override public UpgradePath getPath()         { return UpgradePath.SURVEYOR; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        worker.addStatus(new PhaseStabilizerStatus());
    }
}
