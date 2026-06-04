package game.upgrades.surveyor;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.statuses.RevealedStatus;
import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;
import game.upgrades.UpgradeStation;

import java.util.List;

/**
 * Surveyor Lab Station — offers the Surveyor upgrade track. Displays as {@code ⌖}.
 * <p>
 * Catalogue: Surveyor Optics (T1) → Magboots (T2) → Phase Stabilizer (T3).
 * <p>
 * <strong>Ambient effect (cross-component — status × ground × AoE scan):</strong>
 * each turn, the station's recon pylon sweeps every tile within radius
 * {@value #PYLON_RADIUS} and applies or refreshes a {@link RevealedStatus} on
 * every {@link Ability#IS_SPAWNER IS_SPAWNER} ground it finds. Revealed Holes
 * broadcast "spawn imminent" warnings one turn before they fire (see
 * {@code Hole.tick}), so this station effectively turns the surrounding map
 * into a permanent threat-radar — even before any worker buys an upgrade. The
 * Surveyor Optics upgrade (mobile, smaller radius) and this station (stationary,
 * wider radius) complement each other.
 *
 * @author Jax
 */
public class SurveyorStation extends UpgradeStation {

    private static final int PYLON_RADIUS = 8;
    private static final int REVEAL_DURATION = 2;

    /**
     * @param companyAccount the REQ1 company-credit pool this station charges
     */
    public SurveyorStation(CompanyAccount companyAccount) {
        super('⌖', "Surveyor Lab Station", companyAccount);
    }

    @Override
    public UpgradePath getOfferedPath() {
        return UpgradePath.SURVEYOR;
    }

    @Override
    protected List<Upgrade> catalogue() {
        return List.of(new SurveyorOptics(), new Magboots(), new PhaseStabilizer());
    }

    /**
     * Recon pylon sweep — apply/refresh {@link RevealedStatus} on every spawner
     * within {@value #PYLON_RADIUS} tiles.
     *
     * @param location the station's location
     */
    @Override
    public void tick(Location location) {
        for (Location nearby : location.getNearbyLocations(PYLON_RADIUS)) {
            Ground ground = nearby.getGround();
            if (!ground.hasAbility(Ability.IS_SPAWNER)) continue;

            List<RevealedStatus> existing = ground.statusesOf(RevealedStatus.class);
            if (existing.isEmpty()) {
                ground.addStatus(new RevealedStatus(REVEAL_DURATION));
            } else {
                existing.get(0).refresh(REVEAL_DURATION);
            }
        }
    }
}
