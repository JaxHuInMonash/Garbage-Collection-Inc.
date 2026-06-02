package game.upgrades.combat;

import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;
import game.upgrades.UpgradeStation;

import java.util.List;

/**
 * Combat Lab Station — offers the Combat upgrade track. Displays as {@code ⚔}.
 * <p>
 * Placeholder catalogue: as the Combat upgrades are written, add them to
 * {@link #catalogue()} in tier order.
 *
 * @author Jax
 */
public class CombatStation extends UpgradeStation {

    /**
     * @param companyAccount the REQ1 company-credit pool this station charges
     */
    public CombatStation(CompanyAccount companyAccount) {
        super('⚔', "Combat Lab Station", companyAccount);
    }

    @Override
    public UpgradePath getOfferedPath() {
        return UpgradePath.COMBAT;
    }

    @Override
    protected List<Upgrade> catalogue() {
        // TODO(REQ3): add CombatConditioning (T1), ReinforcedPlates (T2), PlasmaGauntlet (T3).
        return List.of();
    }
}
