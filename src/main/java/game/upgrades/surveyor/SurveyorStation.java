package game.upgrades.surveyor;

import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;
import game.upgrades.UpgradeStation;

import java.util.List;

/**
 * Surveyor Lab Station — offers the Surveyor upgrade track. Displays as {@code ⌖}.
 * <p>
 * Placeholder catalogue: as the Surveyor upgrades are written, add them to
 * {@link #catalogue()} in tier order.
 *
 * @author Jax
 */
public class SurveyorStation extends UpgradeStation {

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
        // TODO(REQ3): add SurveyorOptics (T1), Magboots (T2), PhaseStabilizer (T3).
        return List.of();
    }
}
