package game.statuses;

import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PendingWarpStatus} — the staged warp offer the
 * Phase Stabilizer attaches to a worker for {@code AcceptWarpAction} to consume.
 * Deterministic counter behaviour; uses a mock {@link Location} for the
 * destination.
 */
class PendingWarpStatusTest {

    @Test
    void newStatusIsActiveWhenDurationIsPositive() {
        Location dest = mock(Location.class);
        PendingWarpStatus warp = new PendingWarpStatus(dest, "Test Source", 2);
        assertTrue(warp.isStatusActive());
    }

    @Test
    void destinationAndSourceLabelAreExposed() {
        Location dest = mock(Location.class);
        when(dest.x()).thenReturn(42);
        when(dest.y()).thenReturn(7);

        PendingWarpStatus warp = new PendingWarpStatus(dest, "WarperTree-X", 2);

        assertSame(dest, warp.getDestination());
        assertEquals("WarperTree-X", warp.getSourceLabel());
    }

    @Test
    void ticksDownToInactiveAfterDurationTurns() {
        Location dest = mock(Location.class);
        PendingWarpStatus warp = new PendingWarpStatus(dest, "Test", 2);

        warp.tickStatus(null, null);
        assertTrue(warp.isStatusActive(), "active after 1 tick");
        warp.tickStatus(null, null);
        assertFalse(warp.isStatusActive(), "inactive after 2 ticks");
    }

    @Test
    void edge_zeroDurationStartsInactive() {
        Location dest = mock(Location.class);
        PendingWarpStatus warp = new PendingWarpStatus(dest, "Test", 0);
        assertFalse(warp.isStatusActive());
    }
}
