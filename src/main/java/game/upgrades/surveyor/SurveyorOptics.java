package game.upgrades.surveyor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.display.EventLog;
import game.statuses.RevealedStatus;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

import java.util.List;

/**
 * Surveyor path, Tier 1 — <strong>Optics</strong>.
 * <p>
 * <strong>Per-turn passive (cross-component — actor × status × ground × spawner):</strong>
 * scans every tile within {@value #RADIUS} of the worker. Any ground marked
 * {@link Ability#IS_SPAWNER} (Hole, Vent) is given a {@link RevealedStatus};
 * revealed Holes broadcast a "spawn imminent" warning one turn before they
 * fire (see {@code Hole.tick}).
 * <p>
 * When a previously unrevealed spawner first comes into range, the surveyor
 * logs its location to the {@link EventLog} so the player learns something
 * useful, not just a status flag they can't see.
 *
 * @author Jax
 */
public class SurveyorOptics implements Upgrade {

    private static final int TIER = 1;
    private static final int WORKER_COST = 50;
    private static final int COMPANY_COST = 30;
    private static final int RADIUS = 5;
    private static final int REVEAL_DURATION = 2;

    @Override public String getName()              { return "Surveyor Optics"; }
    @Override public UpgradePath getPath()         { return UpgradePath.SURVEYOR; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        // No one-shot effect — the value is entirely in the per-turn scan.
    }

    @Override
    public void tick(Actor worker, Location location) {
        for (Location nearby : location.getNearbyLocations(RADIUS)) {
            Ground ground = nearby.getGround();
            if (!ground.hasAbility(Ability.IS_SPAWNER)) continue;

            List<RevealedStatus> existing = ground.statusesOf(RevealedStatus.class);
            if (existing.isEmpty()) {
                ground.addStatus(new RevealedStatus(REVEAL_DURATION));
                EventLog.getInstance().log(
                        "Surveyor reveals " + ground + " at ("
                                + nearby.x() + ", " + nearby.y() + ").");
            } else {
                existing.get(0).refresh(REVEAL_DURATION);
            }
        }
    }
}
