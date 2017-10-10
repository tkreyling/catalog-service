package catalog;

import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.event.EventListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(Neo4jProperties.class)
public class Neo4jConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public org.neo4j.ogm.config.Configuration configuration(Neo4jProperties properties) {
        return properties.createConfiguration();
    }

    @Bean
    public SessionFactory sessionFactory(
            org.neo4j.ogm.config.Configuration configuration,
            ApplicationContext applicationContext,
            ObjectProvider<List<EventListener>> eventListeners
    ) {
        SessionFactory sessionFactory = new SessionFactory(configuration,
                // Specify model package explicitly here to avoid warnings about classes that are not graph nodes.
                "catalog.neo4j.category.model");

        List<EventListener> providedEventListeners = eventListeners.getIfAvailable();
        if (providedEventListeners != null) {
            for (EventListener eventListener : providedEventListeners) {
                sessionFactory.register(eventListener);
            }
        }
        return sessionFactory;
    }
}
