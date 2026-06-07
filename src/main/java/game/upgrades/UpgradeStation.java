package game.upgrades;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ApplyUpgradeAction;
import game.capabilities.Progressable;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction #2 of REQ3 (Worker Progression System).
 * <p>
 * A Lab Station is a non-enterable {@link Ground} (interacted with from an
 * adjacent tile, like the Supercomputer). Each concrete station offers exactly
 * one {@link UpgradePath}; it surfaces only the upgrades the adjacent worker is
 * currently eligible for, gated by {@link WorkerProgression#canApply(Upgrade)}.
 * <p>
 * The station depends only on the {@link Upgrade} abstraction and the
 * {@link CompanyAccount} seam — never on concrete upgrade classes beyond the
 * fixed catalogue it declares, and never on the concrete worker class (it uses
 * the {@link Progressable} capability lookup).
 *
 * @author Jax
 */
public abstract class UpgradeStation extends Ground {

    private final CompanyAccount companyAccount;

    /**
     * @param displayChar    map glyph for this station
     * @param name           human-readable name
     * @param companyAccount the REQ1 company-credit pool this station charges
     */
    protected UpgradeStation(char displayChar, String name, CompanyAccount companyAccount) {
        super(displayChar, name);
        this.companyAccount = companyAccount;
    }

    /** @return the single path this station specialises in. */
    public abstract UpgradePath getOfferedPath();

    /**
     * @return every upgrade this station can ever offer, ordered by tier.
     *         The station filters this list per-worker each turn.
     */
    protected abstract List<Upgrade> catalogue();

    /** Stations are consoles — workers interact from an adjacent tile, not by standing on them. */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Offers an {@link ApplyUpgradeAction} for each catalogue upgrade the
     * adjacent worker is eligible to install. Non-worker actors (no
     * {@link Progressable} capability) are offered nothing.
     *
     * @param actor     the adjacent actor
     * @param location  this station's location
     * @param direction direction of this station from the actor
     * @return the eligible upgrade actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        Optional<Progressable> progressable = actor.asCapability(Progressable.class);
        if (progressable.isEmpty()) {
            return actions;
        }

        WorkerProgression progression = progressable.get().getProgression();
        for (Upgrade upgrade : catalogue()) {
            if (progression.canApply(upgrade)) {
                actions.add(new ApplyUpgradeAction(upgrade, progression, companyAccount));
            }
        }
        return actions;
    }
}
