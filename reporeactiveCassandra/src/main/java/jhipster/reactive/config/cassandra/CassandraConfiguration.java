package jhipster.reactive.config.cassandra;

import com.codahale.metrics.MetricRegistry;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;
import io.github.jhipster.config.JHipsterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.config.java.AbstractReactiveCassandraConfiguration;

@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
@Profile({JHipsterConstants.SPRING_PROFILE_DEVELOPMENT, JHipsterConstants.SPRING_PROFILE_PRODUCTION})
public class CassandraConfiguration extends AbstractReactiveCassandraConfiguration {

    @Value("${spring.data.cassandra.protocolVersion:V4}")
    private ProtocolVersion protocolVersion;

    @Override
    protected String getKeyspaceName() {
        return "defaultCassandra";
    }


}
