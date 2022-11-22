package org.food.ordering.domain.valueobject;

import org.food.ordering.domain.exception.BadArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    private final BigDecimal amount;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isGreaterThanZero() {
        return !isAmountNull() &&
               this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money) {
        return !isAmountNull() &&
               !isArgumentNull(money) &&
               this.amount.compareTo(money.getAmount()) > 0;
    }

    public Money subtract(Money money) {
        validateMoneys(money);
        return new Money(setScale(this.amount.subtract(money.getAmount())));
    }

    public Money add(Money money) {
        validateMoneys(money);
        return new Money(setScale(this.amount.add(money.getAmount())));
    }

    public Money multiply(int multiplier) {
        validateAmountField();
        return new Money(setScale(this.amount.multiply(new BigDecimal(multiplier))));
    }

    private void validateMoneys(Money money) {
        validateAmountField();
        validateArgumentField(money);
    }

    private void validateArgumentField(Money money) {
        if (isArgumentNull(money)) {
            throw new BadArgumentException("Cannot process null argument amount");
        }
    }

    private void validateAmountField() {
        if (isAmountNull()) {
            throw new BadArgumentException("Cannot process null amount");
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    private boolean isAmountNull() {
        return this.amount == null;
    }

    private boolean isArgumentNull(Money money) {
        return money.getAmount() == null;
    }

    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
}
