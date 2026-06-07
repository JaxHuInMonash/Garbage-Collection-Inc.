package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.display.EventLog;
import game.statuses.PendingWarpStatus;
import game.statuses.PhaseStabilizerStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stage 2 (final) of the Warper Tree lifecycle. Displays as {@code W}.
 * <p>
 * A fully matured warper tree whose root system is deeply entangled with the
 * moon's unstable teleportation grid. Each turn, if a worker
 * ({@link Ability#ATTACKABLE_BY_CREATURES}) is on an adjacent tile, the tree's
 * roots grab them and warp them to a random valid location anywhere on the map
 * (single-threaded flora rule — only one warp per turn, targeting the first
 * worker found).
 * <p>
 * <strong>Surveyor T3 interaction (Phase Stabilizer):</strong> if the adjacent
 * worker is phase-stable, the tree does <em>not</em> warp them. Instead, it
 * stages a {@link PendingWarpStatus} on the worker carrying the candidate
 * destination — the worker may then accept the warp via
 * {@link game.actions.AcceptWarpAction} on a later turn, or simply let the
 * offer expire. The tree itself stays put and remains hazardous to
 * non-phase-stable workers, preserving its tactical value.
 * <p>
 * Actors cannot enter the mature warper tree tile itself.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 */
public class WarperTreeMature extends Ground {

    private static final Random RANDOM = new Random();
    private static final int WARP_OFFER_DURATION = 2;

    /**
     * Constructor.
     */
    public WarperTreeMature() {
        super('W', "Warper Tree (Mature)");
    }

    /**
     * Actors cannot enter a mature warper tree tile.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, processes the first adjacent worker:
     * <ul>
     *   <li>If the worker is phase-stable, stage a {@link PendingWarpStatus}
     *       carrying a candidate destination — the worker decides whether
     *       to accept on a later turn.</li>
     *   <li>Otherwise, warp them immediately to a random valid location.</li>
     * </ul>
     * Only one worker is processed per turn (single-threaded flora rule).
     *
     * @param location the location of this mature warper tree
     */
    @Override
    public void tick(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (destination.containsAnActor()
                    && destination.getActor().hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                Actor worker = destination.getActor();

                if (worker.hasStatus(PhaseStabilizerStatus.class)) {
                    offerWarp(worker, location);
                } else {
                    warpWorker(worker, location);
                }
                return; // single-threaded flora rule — one worker per turn
            }
        }
    }

    /**
     * Picks a candidate destination and stages it as a {@link PendingWarpStatus}
     * on the worker. They may accept it via {@link game.actions.AcceptWarpAction}
     * on a subsequent turn. The offer auto-expires after {@value #WARP_OFFER_DURATION} turns.
     *
     * @param worker   the phase-stable worker
     * @param location the tree's location (used to derive the map and label)
     */
    private void offerWarp(Actor worker, Location location) {
        Location preview = pickRandomValidDestination(worker, location);
        if (preview == null) return;

        String sourceLabel = "Warper Tree at (" + location.x() + ", " + location.y() + ")";
        worker.addStatus(new PendingWarpStatus(preview, sourceLabel, WARP_OFFER_DURATION));
        EventLog.getInstance().log(
                worker + "'s phase field maps a warp from " + sourceLabel
                        + " → (" + preview.x() + ", " + preview.y() + "). Accept on your next turn.");
    }

    /**
     * Teleports the given worker to a random valid location on the map.
     * A valid destination is any tile the worker can enter that is not
     * already occupied by another actor. If no valid tile exists (extremely
     * unlikely), the warp is silently skipped.
     * Used for the non-phase-stable case (the standard warper behaviour).
     *
     * @param worker   the worker to warp
     * @param location the location of this tree (used to get the map)
     */
    private void warpWorker(Actor worker, Location location) {
        Location destination = pickRandomValidDestination(worker, location);
        if (destination != null) {
            location.map().moveActor(worker, destination);
        }
    }

    /**
     * Collects every tile on the map the worker can validly land on
     * ({@code canActorEnter(worker)} and no existing actor) and returns one
     * at random, or null if none exist.
     */
    private Location pickRandomValidDestination(Actor worker, Location source) {
        GameMap map = source.map();
        List<Location> validDestinations = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location candidate = map.at(x, y);
                if (candidate.canActorEnter(worker) && !candidate.containsAnActor()) {
                    validDestinations.add(candidate);
                }
            }
        }
        if (validDestinations.isEmpty()) return null;
        return validDestinations.get(RANDOM.nextInt(validDestinations.size()));
    }
}
