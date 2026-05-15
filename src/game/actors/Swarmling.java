package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.GameMap;
import game.BasicInventory;
import game.states.swarmling.DormantState;
import game.statuses.ReflectionStatus;
import game.weapons.SwarmlingClaw;
import game.capabilities.Ability;

import java.util.List;

/**
 * A colony fragment that thrives in toxic environments.
 * <p>
 * Display character: {@code Ŝ}. Starting HP: 50.
 * <p>
 * <strong>Environmental Passive:</strong> Loses 1 HP per turn on normal ground;
 * heals 2 HP per turn when standing on Toxic Waste (ability {@link Ability#IS_TOXIC}).
 * <p>
 * The Swarmling uses a formal State Pattern. Its four states are:
 * {@link game.states.swarmling.DormantState},
 * {@link game.states.swarmling.ScoutState},
 * {@link game.states.swarmling.CullingState}, and
 * {@link game.states.swarmling.BerserkState}.
 * <p>
 * <strong>Reflection:</strong> {@link ReflectionStatus} added by DormantState is
 * intercepted in {@link #hurt(int)} so incoming damage is absorbed before
 * reaching HP.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class Swarmling extends StatefulCreature  {
    private static final int INITIAL_HIT_POINTS = 50;

    private static final int PASSIVE_DAMAGE = 1;
    private static final int TOXIC_HEAL = 2;

    /** Constructor. */
    public Swarmling() {
        super("Swarmling", 'Ŝ', INITIAL_HIT_POINTS, new BasicInventory(), new DormantState());
        this.setIntrinsicWeapon(new SwarmlingClaw());
    }

    @Override
    public Action passive(GameMap map) {
        // Environmental passive: toxic waste heals, normal ground hurts.
        // Uses applyPassiveDamage() so the external-damage flag is not set
        // and ReflectionStatus (combat shield) is not consumed.
        if (map.locationOf(this).getGround().hasAbility(Ability.IS_TOXIC)) {
            this.heal(TOXIC_HEAL);
        } else {
            applyPassiveDamage(PASSIVE_DAMAGE);
        }

        return null;
    }

    /**
     * Intercepts damage through any active {@link ReflectionStatus} before it
     * reaches HP. Marks the hit as external damage so ScoutState can detect it.
     */
    @Override
    public void hurt(int damage) {
        markExternalDamage();

        List<ReflectionStatus> shields = statusesOf(ReflectionStatus.class);
        if (shields.isEmpty()) {
            applyPassiveDamage(damage); // flag already set, just apply HP reduction
            return;
        }

        ReflectionStatus shield = shields.get(0);
        int absorbed = shield.absorb(damage);
        int remaining = damage - absorbed;

        if (!shield.isStatusActive())
            removeStatus(shield);

        if (remaining > 0)
            applyPassiveDamage(remaining);
    }
}