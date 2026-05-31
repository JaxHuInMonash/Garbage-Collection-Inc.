package game.upgrades.healer;

import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradeStation;
import game.upgrades.UpgradePath;

import java.util.List;

/**
 * Healer Lab Station (reference implementation) — offers the Healer upgrade
 * track. Displays as {@code ✚}.
 * <p>
 * Only the Healer-path catalogue is exposed; the base {@link UpgradeStation}
 * handles eligibility filtering and action creation. As the other Healer tiers
 * are written, add them to {@link #catalogue()} in tier order.
 *
 * @author Jax
 */
public class HealerStation extends UpgradeStation {

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
}
