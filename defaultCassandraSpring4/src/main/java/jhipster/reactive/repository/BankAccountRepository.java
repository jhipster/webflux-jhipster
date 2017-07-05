package jhipster.reactive.repository;

import jhipster.reactive.domain.BankAccount;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cassandra repository for the BankAccount entity.
 */
@Repository
public class BankAccountRepository {

    private final Session session;

    private final Validator validator;

    private Mapper<BankAccount> mapper;

    private PreparedStatement findAllStmt;

    private PreparedStatement truncateStmt;

    public BankAccountRepository(Session session, Validator validator) {
        this.session = session;
        this.validator = validator;
        this.mapper = new MappingManager(session).mapper(BankAccount.class);
        this.findAllStmt = session.prepare("SELECT * FROM bankAccount");
        this.truncateStmt = session.prepare("TRUNCATE bankAccount");
    }

    public List<BankAccount> findAll() {
        List<BankAccount> bankAccountsList = new ArrayList<>();
        BoundStatement stmt = findAllStmt.bind();
        session.execute(stmt).all().stream().map(
            row -> {
                BankAccount bankAccount = new BankAccount();
                bankAccount.setId(row.getUUID("id"));
                bankAccount.setName(row.getString("name"));
                bankAccount.setBalance(row.getDecimal("balance"));
                return bankAccount;
            }
        ).forEach(bankAccountsList::add);
        return bankAccountsList;
    }

    public BankAccount findOne(UUID id) {
        return mapper.get(id);
    }

    public BankAccount save(BankAccount bankAccount) {
        if (bankAccount.getId() == null) {
            bankAccount.setId(UUID.randomUUID());
        }
        Set<ConstraintViolation<BankAccount>> violations = validator.validate(bankAccount);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        mapper.save(bankAccount);
        return bankAccount;
    }

    public void delete(UUID id) {
        mapper.delete(id);
    }

    public void deleteAll() {
        BoundStatement stmt = truncateStmt.bind();
        session.execute(stmt);
    }
}
