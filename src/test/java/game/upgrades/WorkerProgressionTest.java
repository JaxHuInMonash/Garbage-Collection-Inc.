package game.upgrades;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WorkerProgression} — the state machine that enforces
 * path-lock and tier-sequence rules for the upgrade system. Uses Mockito to
 * stub {@link Upgrade} instances; no real engine objects are needed.
 */
class WorkerProgressionTest {

    private WorkerProgression progression;

    @BeforeEach
    void setUp() {
        progression = new WorkerProgression();
    }

    private Upgrade mockUpgrade(UpgradePath path, int tier) {
        Upgrade u = mock(Upgrade.class);
        when(u.getPath()).thenReturn(path);
        when(u.getTier()).thenReturn(tier);
        return u;
    }

    @Test
    @DisplayName("fresh progression starts at NONE / tier 0")
    void freshProgressionIsUnspecialised() {
        assertEquals(UpgradePath.NONE, progression.getPath());
        assertEquals(0, progression.getTier());
        assertTrue(progression.getInstalled().isEmpty());
    }

    @Test
    @DisplayName("canApply accepts tier 1 of any path on a fresh worker")
    void canApplyAcceptsTier1OnFreshProgression() {
        assertTrue(progression.canApply(mockUpgrade(UpgradePath.FIGHTER, 1)));
        assertTrue(progression.canApply(mockUpgrade(UpgradePath.SURVEYOR, 1)));
        assertTrue(progression.canApply(mockUpgrade(UpgradePath.HEALER, 1)));
    }

    @Test
    @DisplayName("invalid — canApply rejects tier 2 before tier 1 is installed")
    void canApplyRejectsSkippedTier() {
        assertFalse(progression.canApply(mockUpgrade(UpgradePath.FIGHTER, 2)));
        assertFalse(progression.canApply(mockUpgrade(UpgradePath.FIGHTER, 3)));
    }

    @Test
    @DisplayName("invalid — after a FIGHTER tier-1 install, other paths are locked out")
    void canApplyEnforcesPathLock() {
        Actor worker = mock(Actor.class);
        Upgrade fighter1 = mockUpgrade(UpgradePath.FIGHTER, 1);
        progression.install(fighter1, worker);

        // Other-path tier-1s become invalid even though tier is correct.
        assertFalse(progression.canApply(mockUpgrade(UpgradePath.SURVEYOR, 1)));
        assertFalse(progression.canApply(mockUpgrade(UpgradePath.HEALER, 1)));
    }

    @Test
    @DisplayName("install advances tier and commits the path")
    void installCommitsState() {
        Actor worker = mock(Actor.class);
        Upgrade fighter1 = mockUpgrade(UpgradePath.FIGHTER, 1);

        progression.install(fighter1, worker);

        assertEquals(UpgradePath.FIGHTER, progression.getPath());
        assertEquals(1, progression.getTier());
        assertEquals(1, progression.getInstalled().size());
    }

    @Test
    @DisplayName("install delegates to the upgrade's applyTo")
    void installFiresUpgradeApplyTo() {
        Actor worker = mock(Actor.class);
        Upgrade upgrade = mockUpgrade(UpgradePath.HEALER, 1);

        progression.install(upgrade, worker);

        verify(upgrade).applyTo(worker);
    }

    @Test
    @DisplayName("canApply accepts tier 2 on the same path after tier 1")
    void canApplyAcceptsNextTierSamePath() {
        Actor worker = mock(Actor.class);
        progression.install(mockUpgrade(UpgradePath.SURVEYOR, 1), worker);
        assertTrue(progression.canApply(mockUpgrade(UpgradePath.SURVEYOR, 2)));
        // Still rejects tier 3 (skipped tier).
        assertFalse(progression.canApply(mockUpgrade(UpgradePath.SURVEYOR, 3)));
    }

    @Test
    @DisplayName("tick drives every installed upgrade's per-turn passive")
    void tickInvokesAllInstalledTicks() {
        Actor worker = mock(Actor.class);
        Location location = mock(Location.class);
        Upgrade tier1 = mockUpgrade(UpgradePath.HEALER, 1);
        Upgrade tier2 = mockUpgrade(UpgradePath.HEALER, 2);

        progression.install(tier1, worker);
        progression.install(tier2, worker);

        progression.tick(worker, location);

        verify(tier1).tick(worker, location);
        verify(tier2).tick(worker, location);
    }
}
