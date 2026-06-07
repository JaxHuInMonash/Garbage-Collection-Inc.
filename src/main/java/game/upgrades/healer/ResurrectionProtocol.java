package game.upgrades.healer;

import edu.monash.fit2099.engine.actors.Actor;
import game.statuses.ResurrectionProtocolStatus;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

/**
 * Healer path, Tier 3 — <strong>Resurrection Protocol</strong>.
 * <p>
 * <strong>On apply (one-shot):</strong> installs a
 * {@link ResurrectionProtocolStatus} marker on the worker.
 * {@link game.actors.ContractedWorker#unconscious} consults this marker on
 * death — if present, the override replaces the worker's tile with a
 * {@link game.grounds.StasisPod} that holds a reference to the dead worker
 * before deferring to the engine's normal removal.
 * <p>
 * The pod is later consumed by another worker stepping onto it — see
 * {@link game.grounds.StasisPod#tick} for the revival math (HP restored to
 * 10, 30% of the revived worker's personal credits deducted as the Company's
 * "processing fee", pod tile reverts to the ground that was underneath).
 * <p>
 * Cross-component: status marker × death-lifecycle override × ground spawn ×
 * deferred inter-actor revival with persistent worker state across the
 * death/revive transition (upgrades, statuses, inventory all preserved on
 * the Actor instance).
 *
 * @author Jax
 */
public class ResurrectionProtocol implements Upgrade {

    private static final int TIER = 3;
    private static final int WORKER_COST = 300;
    private static final int COMPANY_COST = 250;

    @Override public String getName()              { return "Resurrection Protocol"; }
    @Override public UpgradePath getPath()         { return UpgradePath.HEALER; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        worker.addStatus(new ResurrectionProtocolStatus());
    }
}
