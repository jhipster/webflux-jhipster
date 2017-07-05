package jhipster.reactive.repository;

import jhipster.reactive.domain.Operation;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cassandra repository for the Operation entity.
 */
@Repository
public class OperationRepository {

    private final Session session;

    private final Validator validator;

    private Mapper<Operation> mapper;

    private PreparedStatement findAllStmt;

    private PreparedStatement truncateStmt;

    public OperationRepository(Session session, Validator validator) {
        this.session = session;
        this.validator = validator;
        this.mapper = new MappingManager(session).mapper(Operation.class);
        this.findAllStmt = session.prepare("SELECT * FROM operation");
        this.truncateStmt = session.prepare("TRUNCATE operation");
    }

    public List<Operation> findAll() {
        List<Operation> operationsList = new ArrayList<>();
        BoundStatement stmt = findAllStmt.bind();
        session.execute(stmt).all().stream().map(
            row -> {
                Operation operation = new Operation();
                operation.setId(row.getUUID("id"));
                operation.setDate(row.get("date", Instant.class));
                operation.setDescription(row.getString("description"));
                operation.setAmount(row.getDecimal("amount"));
                return operation;
            }
        ).forEach(operationsList::add);
        return operationsList;
    }

    public Operation findOne(UUID id) {
        return mapper.get(id);
    }

    public Operation save(Operation operation) {
        if (operation.getId() == null) {
            operation.setId(UUID.randomUUID());
        }
        Set<ConstraintViolation<Operation>> violations = validator.validate(operation);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        mapper.save(operation);
        return operation;
    }

    public void delete(UUID id) {
        mapper.delete(id);
    }

    public void deleteAll() {
        BoundStatement stmt = truncateStmt.bind();
        session.execute(stmt);
    }
}
