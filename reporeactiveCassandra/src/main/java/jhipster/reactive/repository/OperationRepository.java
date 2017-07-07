package jhipster.reactive.repository;

import jhipster.reactive.domain.Operation;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Cassandra repository for the Operation entity.
 */
@Repository
public interface OperationRepository extends ReactiveCassandraRepository<Operation,UUID>{

}
