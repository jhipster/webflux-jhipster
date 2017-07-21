package jhipster.reactive.repository;

import jhipster.reactive.domain.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB repository for the Operation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OperationRepository extends ReactiveMongoRepository<Operation,String> {
    Flux<Operation> findAllBy(Pageable pageable);
}
