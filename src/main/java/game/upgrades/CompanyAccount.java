package game.upgrades;

/**
 * Integration seam for the Company Credit pool introduced in <strong>A3 REQ1
 * (Quota)</strong>.
 * <p>
 * REQ3 only needs to know that <em>something</em> holds the company's shared
 * credits and can be charged. It depends on this narrow interface rather than
 * REQ1's concrete quota class, so the two requirements stay decoupled
 * (Dependency Inversion). REQ1's quota manager is expected to implement this
 * interface and be handed to each {@link UpgradeStation} at world set-up.
 * <p>
 * <strong>Coordination note (manager):</strong> the REQ1 owner should make their
 * quota/ledger class {@code implements CompanyAccount}. If REQ1 lands after this
 * sketch, a temporary no-op implementation can be injected so the upgrade
 * stations remain functional during development.
 *
 * @author Jax
 */
public interface CompanyAccount {

    /**
     * @param companyCredits amount to test
     * @return true if the company pool currently holds at least {@code companyCredits}
     */
    boolean canAfford(int companyCredits);

    /**
     * Deducts the given amount from the company pool. Callers must check
     * {@link #canAfford(int)} first.
     *
     * @param companyCredits amount to remove
     */
    void deduct(int companyCredits);
}
