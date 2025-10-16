package org.osimptest.app.services.impl;

import com.zaxxer.hikari.HikariDataSource;
import org.osimp.api.ReleaseConnection;
import org.osimptest.app.services.ExternalServiceClient;
import org.springframework.stereotype.Service;

@Service
public class ExternalServiceClientImpl implements ExternalServiceClient {
    private final HikariDataSource dataSource;

    public ExternalServiceClientImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @ReleaseConnection
    public int getCurrentActiveConnectionsNumber() {
        return dataSource.getHikariPoolMXBean().getActiveConnections();
    }
}
