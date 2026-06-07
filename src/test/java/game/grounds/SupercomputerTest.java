package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import game.quota.QuotaManager;
import game.TestActors;
import game.TestMaps;
import game.spawning.SlimeSpawnEffect;
import game.spawning.SpawnEffectManager;
import game.actors.Slime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Supercomputer} (REQ1 Section G).
 *
 * <p>Coverage: display char, entry blocked, implements QuotaListener,
 * fires adjacent worker on deadline missed, does NOT fire non-adjacent worker,
 * quota met callback does not throw, full quota cycle via deposits.
 * @author Mary
 */
class SupercomputerTest {

    @BeforeEach
    void setUp() {
        QuotaManager.resetInstance();
        SpawnEffectManager.resetInstance();
        SpawnEffectManager.getInstance().registerEffect(Slime.class, new SlimeSpawnEffect());
    }

    // ── Basic properties ─────────────────────────────────────────────────────

    /** Typical: display character is '≡'. */
    @Test
    void testSupercomputerDisplayChar() {
        Supercomputer sc = new Supercomputer();
        assertEquals('≡', sc.getDisplayChar());
    }

    /** Typical: actors cannot enter the Supercomputer tile. */
    @Test
    void testSupercomputerBlocksEntry() {
        Supercomputer sc = new Supercomputer();
        assertFalse(sc.canActorEnter(null));
    }

    /** Typical: toString returns "Supercomputer". */
    @Test
    void testSupercomputerToString() {
        Supercomputer sc = new Supercomputer();
        assertEquals("Supercomputer", sc.toString());
    }

    // ── QuotaListener ────────────────────────────────────────────────────────

    /**
     * Verifies Supercomputer responds to quota events by confirming
     * onQuotaMet can be called without error after registering with QuotaManager.
     */
    @Test
    void testSupercomputerReactsToQuotaMetEvent() {
        boolean[] called = {false};
        // Add a second listener to confirm the event was fired
        QuotaManager.getInstance().addListener(new game.quota.QuotaListener() {
            @Override public void onQuotaMet(int r, int t, int tl, String msg) { called[0] = true; }
            @Override public void onDeadlineMissed(String msg) {}
        });

        new Supercomputer(); // registers itself with QuotaManager
        QuotaManager.getInstance().addCompanyCredits(100); // triggers onQuotaMet

        assertTrue(called[0]);
    }

    /** Typical: onQuotaMet does not throw. */
    @Test
    void testOnQuotaMetDoesNotThrow() {
        Supercomputer sc = new Supercomputer();
        assertDoesNotThrow(() ->
                sc.onQuotaMet(2, 105, 221, "[QUOTA] Quota met!"));
    }

    /** Typical: onDeadlineMissed does not throw. */
    @Test
    void testOnDeadlineMissedDoesNotThrow() {
        Supercomputer sc = new Supercomputer();
        assertDoesNotThrow(() ->
                sc.onDeadlineMissed("[QUOTA] Deadline missed!"));
    }

    // ── Firing workers ───────────────────────────────────────────────────────

    /**
     * Typical: Supercomputer makes adjacent worker unconscious
     * when the deadline is missed.
     */
    @Test
    void testSupercomputerFiresAdjacentWorkerOnDeadlineMissed() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Supercomputer sc = new Supercomputer();
        map.at(2, 2).setGround(sc);

        ContractedWorker worker = TestActors.worker();
        map.addActor(worker, map.at(2, 3));

        sc.onDeadlineMissed("[QUOTA] Deadline missed!");
        sc.tick(map.at(2, 2));

        assertFalse(map.contains(worker));
    }

    /**
     * Typical: worker NOT adjacent to the Supercomputer is NOT fired
     * when the deadline is missed.
     */
    @Test
    void testSupercomputerDoesNotFireNonAdjacentWorker() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Supercomputer sc = new Supercomputer();
        map.at(2, 2).setGround(sc);

        ContractedWorker worker = TestActors.worker();
        map.addActor(worker, map.at(0, 0));

        sc.onDeadlineMissed("[QUOTA] Deadline missed!");
        sc.tick(map.at(2, 2));

        assertTrue(worker.isConscious());
    }

    /**
     * Edge: deadline missed with no adjacent workers does not throw.
     */
    @Test
    void testDeadlineMissedWithNoAdjacentWorkersDoesNotThrow() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Supercomputer sc = new Supercomputer();
        map.at(2, 2).setGround(sc);

        assertDoesNotThrow(() -> {
            sc.onDeadlineMissed("[QUOTA] Deadline missed!");
            sc.tick(map.at(2, 2));
        });
    }

    // ── Full quota cycle ─────────────────────────────────────────────────────

    /**
     * Typical: depositing enough credits advances the rank via QuotaManager.
     * Confirms the full deposit → quota cycle works end-to-end.
     */
    @Test
    void testDepositingEnoughCreditsAdvancesRank() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        ContractedWorker worker = TestActors.worker();
        map.addActor(worker, map.at(2, 2));

        // 50 + 50 = 100 → meets quota → rank 2
        game.items.AluminiumScrap scrap1 = new game.items.AluminiumScrap();
        game.items.AluminiumScrap scrap2 = new game.items.AluminiumScrap();
        worker.getInventory().add(scrap1);
        worker.getInventory().add(scrap2);

        new game.actions.DepositAction(scrap1).execute(worker, map);
        new game.actions.DepositAction(scrap2).execute(worker, map);

        assertEquals(2, QuotaManager.getInstance().getCompanyRank());
    }
}