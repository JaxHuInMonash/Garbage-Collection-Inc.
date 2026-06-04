package game.upgrades.healer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.statuses.BurnStatus;
import game.statuses.PoisonStatus;
import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradeStation;
import game.upgrades.UpgradePath;

import java.util.List;

/**
 * Healer Lab Station — offers the Healer upgrade track. Displays as {@code ✚}.
 * <p>
 * Only the Healer-path catalogue is exposed; the base {@link UpgradeStation}
 * handles eligibility filtering and action creation. Currently the catalogue
 * holds the reference T1 upgrade only — T2 ({@code CleanseField}) and T3
 * ({@code ResurrectionProtocol}) are tracked as TODOs.
 * <p>
 * <strong>Ambient effect (cross-component — actor × status removal × AoE):</strong>
 * each turn, every worker within radius {@value #SANCTUARY_RADIUS} is healed for
 * {@value #SANCTUARY_HEAL} HP <em>and</em> has the first active
 * {@link PoisonStatus} or {@link BurnStatus} stripped from them. Pure healing
 * alone would be trivial — the status removal makes this a genuine
 * cross-component intervention (interacting with the timed-damage pipeline that
 * other systems rely on). The Healer BioSync upgrade (worker-mobile) and this
 * station (stationary sanctuary) compose as a mobile-vs-base pair.
 *
 * @author Jax
 */
public class HealerStation extends UpgradeStation {

    private static final int SANCTUARY_RADIUS = 2;
    private static final int SANCTUARY_HEAL = 1;

    /**
     * @param companyAccount the REQ1 company-credit pool to charge for upgrades
     */
    public HealerStation(CompanyAccount companyAccount) {
        super('✚', "Healer Lab Station", companyAccount);
    }

    @Override
    public UpgradePath getOfferedPath() {
        return UpgradePath.HEALER;
    }

    @Override
    protected List<Upgrade> catalogue() {
        // Tier-ordered. TODO(REQ3): add CleanseField (T2) and ResurrectionProtocol (T3).
        return List.of(new HealerBioSync());
    }

    /**
     * Bio-sanctuary pulse — heal nearby workers and strip one negative status.
     *
     * @param location the station's location
     */
    @Override
    public void tick(Location location) {
        for (Location nearby : location.getNearbyLocations(SANCTUARY_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            Actor actor = nearby.getActor();
            if (!actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) continue;

            actor.heal(SANCTUARY_HEAL);
            stripFirstNegativeStatus(actor);
        }
    }

    /** Removes the first active {@link PoisonStatus}, or otherwise the first {@link BurnStatus}. */
    private void stripFirstNegativeStatus(Actor actor) {
        List<PoisonStatus> poisons = actor.statusesOf(PoisonStatus.class);
        if (!poisons.isEmpty()) {
            actor.removeStatus(poisons.get(0));
            return;
        }
        List<BurnStatus> burns = actor.statusesOf(BurnStatus.class);
        if (!burns.isEmpty()) {
            actor.removeStatus(burns.get(0));
        }
    }
}
