package jhipster.reactive.repository;

import jhipster.reactive.domain.BankAccount;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


/**
 * Cassandra repository for the BankAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BankAccountRepository extends ReactiveCassandraRepository<BankAccount,UUID>{

}
