package jhipster.reactive.repository;

import jhipster.reactive.domain.BankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the BankAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BankAccountRepository extends ReactiveMongoRepository<BankAccount,String> {

}
