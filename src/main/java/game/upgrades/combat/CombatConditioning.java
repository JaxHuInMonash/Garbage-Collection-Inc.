package game.upgrades.combat;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.statuses.RetaliationStatus;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

/**
 * Combat path, Tier 1 — <strong>Combat Conditioning</strong>.
 * <p>
 * <strong>On apply (one-shot):</strong>
 * <ul>
 *   <li>+{@value #MAX_HP_BONUS} to max HP. {@code modifyStatisticMaximum(...INCREASE...)}
 *       also restores current HP to the new max (see {@code BaseStatistic.increaseMaximum}).</li>
 *   <li>Installs a {@link RetaliationStatus} on the worker — every hit has a
 *       {@value #RETALIATION_CHANCE_PERCENT}% chance to inflict {@link game.statuses.BurnStatus}
 *       on the attacker, delivered polymorphically through
 *       {@link game.actions.AttackAction}'s post-hit feedback pipeline.</li>
 * </ul>
 * Cross-component: status × combat-action × damage feedback to attacker.
 *
 * @author Jax
 */
public class CombatConditioning implements Upgrade {

    private static final int TIER = 1;
    private static final int WORKER_COST = 50;
    private static final int COMPANY_COST = 30;

    private static final int MAX_HP_BONUS = 5;
    private static final int RETALIATION_CHANCE_PERCENT = 40;
    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 3;

    @Override public String getName()              { return "Combat Conditioning"; }
    @Override public UpgradePath getPath()         { return UpgradePath.COMBAT; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        worker.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HP_BONUS);
        worker.addStatus(new RetaliationStatus(RETALIATION_CHANCE_PERCENT, BURN_DAMAGE, BURN_DURATION));
    }
}
