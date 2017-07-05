package jhipster.reactive.repository;

import jhipster.reactive.domain.Operation;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Operation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OperationRepository extends MongoRepository<Operation,String> {
    
}
