package game.upgrades;

/**
 * A minimal in-memory {@link CompanyAccount} that simply tracks a single
 * integer balance. Intended as a <strong>temporary scaffold</strong> so the
 * upgrade stations can be wired and exercised before A3 REQ1's quota system
 * lands.
 * <p>
 * <strong>Once REQ1 is in place</strong>, replace every construction of this
 * class with the REQ1 quota manager (which will implement {@link CompanyAccount}).
 * Until then, this lets the lab-station flow be tested end-to-end.
 *
 * @author Jax
 */
public class InMemoryCompanyAccount implements CompanyAccount {

    private int balance;

    /**
     * @param initialBalance starting company credits in this scaffold ledger
     */
    public InMemoryCompanyAccount(int initialBalance) {
        this.balance = initialBalance;
    }

    /** @return current balance (exposed for tests / display). */
    public int getBalance() {
        return balance;
    }

    /**
     * Used by tests and any future deposit-flow code; not part of the
     * {@link CompanyAccount} contract.
     */
    public void deposit(int companyCredits) {
        this.balance += companyCredits;
    }

    @Override
    public boolean canAfford(int companyCredits) {
        return balance >= companyCredits;
    }

    @Override
    public void deduct(int companyCredits) {
        if (!canAfford(companyCredits)) {
            throw new IllegalStateException("Insufficient company credits — caller must check canAfford() first.");
        }
        this.balance -= companyCredits;
    }
}
