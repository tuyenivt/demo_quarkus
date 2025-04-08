package com.example.idgenerator.health;

import com.example.idgenerator.service.UniqueIdGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Liveness
@ApplicationScoped
public class IdGeneratorHealthCheck implements HealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(IdGeneratorHealthCheck.class);

    @Inject
    UniqueIdGenerator idGenerator;

    @Override
    public HealthCheckResponse call() {
        try {
            // Try to generate an ID to verify the service is working
            idGenerator.generateId();
            return HealthCheckResponse.up("ID Generator Service");
        } catch (Exception e) {
            LOG.error("Health check failed", e);
            return HealthCheckResponse.named(("ID Generator Service")).down()
                    .withData("error", e.getMessage()).build();
        }
    }
} 