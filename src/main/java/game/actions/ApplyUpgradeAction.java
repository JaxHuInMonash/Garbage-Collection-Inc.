package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.WorkerProgression;

/**
 * Installs an {@link Upgrade} onto a worker at an
 * {@link game.upgrades.UpgradeStation}.
 * <p>
 * Order of operations (mirrors the corrected {@code BuyAction} flow):
 * <ol>
 *   <li>re-validate eligibility (path lock + tier sequence);</li>
 *   <li>check worker credits;</li>
 *   <li>check company credits via the {@link CompanyAccount} seam;</li>
 *   <li>deduct both currencies;</li>
 *   <li>commit the upgrade through {@link WorkerProgression#install(Upgrade, Actor)}.</li>
 * </ol>
 * Nothing is charged unless all checks pass, so a failed install never leaves
 * the worker out-of-pocket.
 *
 * @author Jax
 */
public class ApplyUpgradeAction extends Action {

    private final Upgrade upgrade;
    private final WorkerProgression progression;
    private final CompanyAccount companyAccount;

    /**
     * @param upgrade        the upgrade to install
     * @param progression    the standing worker's progression state
     * @param companyAccount the company credit pool to charge
     */
    public ApplyUpgradeAction(Upgrade upgrade, WorkerProgression progression, CompanyAccount companyAccount) {
        this.upgrade = upgrade;
        this.progression = progression;
        this.companyAccount = companyAccount;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!progression.canApply(upgrade)) {
            return actor + " cannot install " + upgrade.getName()
                    + " (wrong path or tier out of sequence).";
        }

        int workerCost = upgrade.getWorkerCreditCost();
        int companyCost = upgrade.getCompanyCreditCost();

        if (actor.getStatistic(ActorStatistics.CREDITS) < workerCost) {
            return actor + " lacks the worker credits for " + upgrade.getName()
                    + " (needs " + workerCost + ").";
        }
        if (!companyAccount.canAfford(companyCost)) {
            return actor + " — the company ledger can't cover " + upgrade.getName()
                    + " (needs " + companyCost + " company credits).";
        }

        actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, workerCost);
        companyAccount.deduct(companyCost);
        progression.install(upgrade, actor);

        return actor + " installs " + upgrade.getName()
                + " (" + upgrade.getPath() + " tier " + upgrade.getTier() + ").";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " trains " + upgrade.getName()
                + " [" + upgrade.getWorkerCreditCost() + "W + "
                + upgrade.getCompanyCreditCost() + "C]";
    }
}
