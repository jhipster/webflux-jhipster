package jhipster.reactive.domain;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A BankAccount.
 */
@Table("bankAccount")
public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @PrimaryKey
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal balance;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BankAccount bankAccount = (BankAccount) o;
        if (bankAccount.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bankAccount.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BankAccount{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", balance='" + getBalance() + "'" +
            "}";
    }
}
