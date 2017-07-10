package jhipster.reactive.repository;

import jhipster.reactive.domain.Label;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Label entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LabelRepository extends ReactiveMongoRepository<Label,String> {

}
