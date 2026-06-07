package game.upgrades.healer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.display.EventLog;
import game.grounds.Floor;
import game.statuses.CleanseFieldStatus;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

import java.util.HashMap;
import java.util.Map;

/**
 * Healer path, Tier 2 — <strong>Cleanse Field</strong>.
 * <p>
 * <strong>On apply (one-shot):</strong> installs a {@link CleanseFieldStatus}
 * marker on the worker. {@link game.actors.ContractedWorker#addStatus} consults
 * this marker and silently rejects any incoming
 * {@link game.statuses.TimedDamageStatus} (Poison, Burn) — that's the immunity
 * half of the upgrade.
 * <p>
 * <strong>Per-turn passive (cross-component — terrain modification × multi-tile
 * scan × persistent state):</strong> tracks each adjacent {@link Ability#ON_FIRE}
 * or {@link Ability#IS_TOXIC} tile in a "cleanse progress" map. A tile that is
 * adjacent for {@value #CLEANSE_TURNS_REQUIRED} consecutive turns is permanently
 * reverted to {@link Floor}. If the worker walks away before the threshold,
 * progress resets — only sustained adjacency cleanses.
 *
 * @author Jax
 */
public class CleanseField implements Upgrade {

    private static final int TIER = 2;
    private static final int WORKER_COST = 150;
    private static final int COMPANY_COST = 100;
    private static final int CLEANSE_TURNS_REQUIRED = 2;

    /** Per-tile adjacency counter. Reset each turn for tiles no longer adjacent. */
    private Map<Location, Integer> cleanseProgress = new HashMap<>();

    @Override public String getName()              { return "Cleanse Field"; }
    @Override public UpgradePath getPath()         { return UpgradePath.HEALER; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        worker.addStatus(new CleanseFieldStatus());
    }

    @Override
    public void tick(Actor worker, Location location) {
        Map<Location, Integer> nextProgress = new HashMap<>();
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!ground.hasAbility(Ability.ON_FIRE) && !ground.hasAbility(Ability.IS_TOXIC)) {
                continue;
            }

            int progress = cleanseProgress.getOrDefault(adjacent, 0) + 1;
            if (progress >= CLEANSE_TURNS_REQUIRED) {
                adjacent.setGround(new Floor());
                EventLog.getInstance().log(
                        worker + "'s cleanse field purifies the tile at ("
                                + adjacent.x() + ", " + adjacent.y() + ").");
                // purified — don't carry progress forward
            } else {
                nextProgress.put(adjacent, progress);
            }
        }
        cleanseProgress = nextProgress;
    }
}
