package jhipster.reactive.repository;

import jhipster.reactive.domain.Label;
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
 * Cassandra repository for the Label entity.
 */
@Repository
public class LabelRepository {

    private final Session session;

    private final Validator validator;

    private Mapper<Label> mapper;

    private PreparedStatement findAllStmt;

    private PreparedStatement truncateStmt;

    public LabelRepository(Session session, Validator validator) {
        this.session = session;
        this.validator = validator;
        this.mapper = new MappingManager(session).mapper(Label.class);
        this.findAllStmt = session.prepare("SELECT * FROM label");
        this.truncateStmt = session.prepare("TRUNCATE label");
    }

    public List<Label> findAll() {
        List<Label> labelsList = new ArrayList<>();
        BoundStatement stmt = findAllStmt.bind();
        session.execute(stmt).all().stream().map(
            row -> {
                Label label = new Label();
                label.setId(row.getUUID("id"));
                label.setLabel(row.getString("label"));
                return label;
            }
        ).forEach(labelsList::add);
        return labelsList;
    }

    public Label findOne(UUID id) {
        return mapper.get(id);
    }

    public Label save(Label label) {
        if (label.getId() == null) {
            label.setId(UUID.randomUUID());
        }
        Set<ConstraintViolation<Label>> violations = validator.validate(label);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        mapper.save(label);
        return label;
    }

    public void delete(UUID id) {
        mapper.delete(id);
    }

    public void deleteAll() {
        BoundStatement stmt = truncateStmt.bind();
        session.execute(stmt);
    }
}
