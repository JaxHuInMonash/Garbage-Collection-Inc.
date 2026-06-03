package game.upgrades.fighter;

import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;
import game.upgrades.UpgradeStation;

import java.util.List;

/**
 * Fighter Lab Station — offers the Fighter upgrade track. Displays as {@code ⚔}.
 * <p>
 * Catalogue is tier-ordered: Combat Conditioning (T1) → Reinforced Plates (T2)
 * → Plasma Gauntlet (T3). Eligibility (path lock + tier sequence) is enforced
 * by the {@link UpgradeStation} base class via {@link game.upgrades.WorkerProgression}.
 *
 * @author Jax
 */
public class FighterStation extends UpgradeStation {

    /**
     * @param companyAccount the REQ1 company-credit pool this station charges
     */
    public FighterStation(CompanyAccount companyAccount) {
        super('⚔', "Fighter Lab Station", companyAccount);
    }

    @Override
    public UpgradePath getOfferedPath() {
        return UpgradePath.FIGHTER;
    }

    @Override
    protected List<Upgrade> catalogue() {
        return List.of(new CombatConditioning(), new ReinforcedPlates(), new PlasmaGauntlet());
    }
}
