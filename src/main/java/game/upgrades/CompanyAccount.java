package game.upgrades;

/**
 * Integration seam between REQ3 (Worker Progression / Upgrade System) and
 * REQ1 (Quota Cycle).
 * <p>
 * REQ3 needs to debit the company-credit pool when a worker purchases an
 * upgrade at a Lab Station. Instead of depending directly on REQ1's
 * {@link game.quota.QuotaManager} singleton — a high-level → low-level
 * coupling that would invert the Dependency Inversion Principle — REQ3
 * depends on this narrow capability interface. REQ1's quota manager
 * implements it; if REQ1 ever swaps its ledger implementation, REQ3 is
 * unaffected.
 * <p>
 * Method contract:
 * <ul>
 *   <li>{@link #canAfford(int)} reports whether the pool currently holds at
 *       least the requested amount of credits.</li>
 *   <li>{@link #deduct(int)} removes the given amount from the pool. Callers
 *       must check {@link #canAfford(int)} first; the implementation may
 *       throw if invoked while underfunded.</li>
 * </ul>
 * Owned by REQ3 (the consumer); fulfilled by REQ1's
 * {@link game.quota.QuotaManager} (the implementer).
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
