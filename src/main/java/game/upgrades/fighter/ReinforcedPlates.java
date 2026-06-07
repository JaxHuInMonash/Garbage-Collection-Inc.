package game.upgrades.fighter;

import edu.monash.fit2099.engine.actors.Actor;
import game.statuses.ReinforcedPlatesStatus;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

/**
 * Combat path, Tier 2 — <strong>Reinforced Plates</strong>.
 * <p>
 * <strong>On apply (one-shot):</strong> installs a {@link ReinforcedPlatesStatus}.
 * The plates absorb 50% of each incoming hit; every 5 absorbed hits arms an AoE
 * shockwave that fires on the bearer's next turn, damaging adjacent hostiles.
 * <p>
 * Cross-component: per-hit absorption (in {@code hurt}) × deferred AoE discharge
 * (in {@code tickStatus}) × terrain-aware target filtering.
 *
 * @author Jax
 */
public class ReinforcedPlates implements Upgrade {

    private static final int TIER = 2;
    private static final int WORKER_COST = 150;
    private static final int COMPANY_COST = 100;

    @Override public String getName()              { return "Reinforced Plates"; }
    @Override public UpgradePath getPath()         { return UpgradePath.FIGHTER; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        worker.addStatus(new ReinforcedPlatesStatus());
    }
}
