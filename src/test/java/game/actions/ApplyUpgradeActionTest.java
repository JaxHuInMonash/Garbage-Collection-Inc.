package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;
import game.upgrades.WorkerProgression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ApplyUpgradeAction} — the central buy-an-upgrade
 * pipeline. Verifies all four guard conditions (path lock / tier sequence,
 * worker credits, company credits, success) and confirms the "charge nothing
 * unless everything passes" discipline.
 */
class ApplyUpgradeActionTest {

    private static final int WORKER_COST = 50;
    private static final int COMPANY_COST = 30;

    private Actor actor;
    private GameMap map;
    private Upgrade upgrade;
    private WorkerProgression progression;
    private CompanyAccount account;

    @BeforeEach
    void setUp() {
        actor = mock(Actor.class);
        map = mock(GameMap.class);
        upgrade = mock(Upgrade.class);
        progression = mock(WorkerProgression.class);
        account = mock(CompanyAccount.class);

        when(upgrade.getWorkerCreditCost()).thenReturn(WORKER_COST);
        when(upgrade.getCompanyCreditCost()).thenReturn(COMPANY_COST);
        when(upgrade.getName()).thenReturn("Test Upgrade");
        when(upgrade.getPath()).thenReturn(UpgradePath.HEALER);
        when(upgrade.getTier()).thenReturn(1);
    }

    @Test
    @DisplayName("happy path — all checks pass: both currencies deducted, install fires")
    void happyPath() {
        when(progression.canApply(upgrade)).thenReturn(true);
        when(actor.getStatistic(ActorStatistics.CREDITS)).thenReturn(100);
        when(account.canAfford(COMPANY_COST)).thenReturn(true);

        ApplyUpgradeAction action = new ApplyUpgradeAction(upgrade, progression, account);
        String result = action.execute(actor, map);

        verify(actor).modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, WORKER_COST);
        verify(account).deduct(COMPANY_COST);
        verify(progression).install(upgrade, actor);
        assertTrue(result.contains("installs"));
    }

    @Test
    @DisplayName("invalid — path-locked / wrong-tier: nothing charged, nothing installed")
    void rejectsIneligibleUpgrade() {
        when(progression.canApply(upgrade)).thenReturn(false);

        ApplyUpgradeAction action = new ApplyUpgradeAction(upgrade, progression, account);
        String result = action.execute(actor, map);

        verify(actor, never()).modifyStatistic(any(), any(), anyInt());
        verify(account, never()).deduct(anyInt());
        verify(progression, never()).install(any(), any());
        assertTrue(result.contains("cannot install"));
    }

    @Test
    @DisplayName("invalid — insufficient worker credits: nothing charged, nothing installed")
    void rejectsInsufficientWorkerCredits() {
        when(progression.canApply(upgrade)).thenReturn(true);
        when(actor.getStatistic(ActorStatistics.CREDITS)).thenReturn(10); // below WORKER_COST

        ApplyUpgradeAction action = new ApplyUpgradeAction(upgrade, progression, account);
        String result = action.execute(actor, map);

        verify(actor, never()).modifyStatistic(any(), any(), anyInt());
        verify(account, never()).deduct(anyInt());
        verify(progression, never()).install(any(), any());
        assertTrue(result.contains("lacks the worker credits"));
    }

    @Test
    @DisplayName("invalid — insufficient company credits: nothing charged, nothing installed")
    void rejectsInsufficientCompanyCredits() {
        when(progression.canApply(upgrade)).thenReturn(true);
        when(actor.getStatistic(ActorStatistics.CREDITS)).thenReturn(100);
        when(account.canAfford(COMPANY_COST)).thenReturn(false);

        ApplyUpgradeAction action = new ApplyUpgradeAction(upgrade, progression, account);
        String result = action.execute(actor, map);

        verify(actor, never()).modifyStatistic(any(), any(), anyInt());
        verify(account, never()).deduct(anyInt());
        verify(progression, never()).install(any(), any());
        assertTrue(result.contains("company ledger"));
    }

    @Test
    @DisplayName("edge — worker has exactly the worker-cost amount: succeeds")
    void exactWorkerCreditBoundary() {
        when(progression.canApply(upgrade)).thenReturn(true);
        when(actor.getStatistic(ActorStatistics.CREDITS)).thenReturn(WORKER_COST);
        when(account.canAfford(COMPANY_COST)).thenReturn(true);

        ApplyUpgradeAction action = new ApplyUpgradeAction(upgrade, progression, account);
        action.execute(actor, map);

        verify(progression).install(upgrade, actor);
    }
}
