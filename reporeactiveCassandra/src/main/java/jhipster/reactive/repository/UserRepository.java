package jhipster.reactive.repository;

import jhipster.reactive.domain.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Cassandra repository for the User entity.
 */
@Repository
public interface UserRepository extends CassandraRepository<User,String> {

    @Query("SELECT * FROM user WHERE activationKey=?0 ALLOW FILTERING;")
    Optional<User> findByActivationKey(String activationKey);

    @Query("SELECT * FROM user WHERE resetKey=?0 ALLOW FILTERING;")
    Optional<User> findByResetKey(String resetKey);

    @Query("SELECT * FROM user WHERE email=?0 ALLOW FILTERING;")
    Optional<User> findByEmail(String email);

    @Query("SELECT * FROM user WHERE login=?0 ALLOW FILTERING;")
    Optional<User> findByLogin(String login);


}
