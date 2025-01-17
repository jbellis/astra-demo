package org.datastax.vsdemo.indexing;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IndexingTableInitializer {
    private final CqlSession session;

    @Value("${astra.cql.driver-config.basic.session-keyspace}")
    private String keyspace;

    public IndexingTableInitializer(AstraClient astra) {
        this.session = astra.cqlSession();
    }

    @PostConstruct
    public void initialize() {
        this.session.execute("""
            CREATE TABLE IF NOT EXISTS %s.indexing (user_id text, text_id uuid, embedding vector<float, 384>, text text, url text, PRIMARY KEY (user_id, text_id))
        """.formatted(keyspace));

        this.session.execute("""
            CREATE CUSTOM INDEX IF NOT EXISTS ann_index ON %s.indexing (embedding) USING 'StorageAttachedIndex'
        """.formatted(keyspace));
    }
}
