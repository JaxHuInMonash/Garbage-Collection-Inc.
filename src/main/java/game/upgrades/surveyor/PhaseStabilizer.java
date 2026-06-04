package game.upgrades.surveyor;

import edu.monash.fit2099.engine.actors.Actor;
import game.statuses.PhaseStabilizerStatus;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

/**
 * Surveyor path, Tier 3 — <strong>Phase Stabilizer</strong>.
 * <p>
 * <strong>On apply (one-shot):</strong> installs a {@link PhaseStabilizerStatus}.
 * The status drives three distinct cross-component effects:
 * <ul>
 *   <li><strong>Wall phasing</strong> — holds a regenerating phase-charge
 *       (every {@value PhaseStabilizerStatus#RECHARGE_TURNS} turns).
 *       {@link game.actors.ContractedWorker#playTurn} offers a
 *       {@link game.actions.PhaseThroughWallAction} for each adjacent wall
 *       when charged; the wall is permanently converted to floor.</li>
 *   <li><strong>Controlled warper</strong> — {@link game.grounds.WarperTreeMature}
 *       no longer warps the worker on contact. Instead, it stages a
 *       {@link game.statuses.PendingWarpStatus} carrying the candidate
 *       destination; the worker may accept the warp via
 *       {@link game.actions.AcceptWarpAction} on a later turn, or let it
 *       expire. The tree is preserved for future use.</li>
 *   <li><strong>Targeted circle</strong> — {@link game.grounds.MagicCircle}
 *       no longer randomises the destination for the worker. Instead, the
 *       menu shows one option per other circle on the map (via
 *       {@link game.actions.UseMagicCircleTargetedAction}), so the worker
 *       picks exactly where to land. Regular workers still get the original
 *       random behaviour.</li>
 * </ul>
 * The unifying theme: phase stability strips the randomness out of the moon's
 * teleportation grid, giving the worker precise control over warps that would
 * otherwise be forced or random.
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
