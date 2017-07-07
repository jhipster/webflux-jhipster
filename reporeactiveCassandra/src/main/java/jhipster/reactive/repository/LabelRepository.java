package jhipster.reactive.repository;

import jhipster.reactive.domain.Label;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

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
public interface LabelRepository extends ReactiveCassandraRepository<Label,UUID>{

}
