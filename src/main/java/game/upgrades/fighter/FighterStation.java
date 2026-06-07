package game.upgrades.fighter;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.display.EventLog;
import game.grounds.Fire;
import game.upgrades.CompanyAccount;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;
import game.upgrades.UpgradeStation;

import java.util.List;
import java.util.Random;

/**
 * Fighter Lab Station — offers the Fighter upgrade track. Displays as {@code ⚔}.
 * <p>
 * Catalogue is tier-ordered: Combat Conditioning (T1) → Reinforced Plates (T2)
 * → Plasma Gauntlet (T3). Eligibility (path lock + tier sequence) is enforced
 * by the {@link UpgradeStation} base class via {@link game.upgrades.WorkerProgression}.
 * <p>
 * <strong>Ambient effect (cross-component — terrain modification × AoE × delayed
 * status):</strong> each turn, the forge has a {@value #FORGE_SPARK_CHANCE_PERCENT}%
 * chance of <em>sparking</em>. When it sparks, every non-wall, non-fire adjacent
 * tile is replaced with {@link Fire} for {@value #FORGE_FIRE_DURATION} turns —
 * any actor stepping on those tiles takes burn damage via the existing
 * {@link game.statuses.BurnStatus} pipeline. The station thus radiates a low,
 * unpredictable combat-zone hazard around itself even when no worker is
 * interacting with it.
 *
 * @author Jax
 */
public class FighterStation extends UpgradeStation {

    private static final int FORGE_SPARK_CHANCE_PERCENT = 8;
    private static final int FORGE_FIRE_DURATION = 3;
    private static final Random RANDOM = new Random();

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

    /**
     * Periodic forge spark — ignites every passable adjacent tile.
     *
     * @param location the station's location
     */
    @Override
    public void tick(Location location) {
        if (RANDOM.nextInt(100) >= FORGE_SPARK_CHANCE_PERCENT) {
            return;
        }
        boolean ignitedAny = false;
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!ground.hasAbility(Ability.BLOCKS_ENTRY) && !ground.hasAbility(Ability.ON_FIRE)) {
                adjacent.setGround(new Fire(ground, FORGE_FIRE_DURATION));
                ignitedAny = true;
            }
        }
        if (ignitedAny) {
            EventLog.getInstance().log(
                    "The Fighter Forge at (" + location.x() + ", " + location.y()
                            + ") sparks — adjacent tiles ignite for " + FORGE_FIRE_DURATION + " turns.");
        }
    }
}
