package org.osimp.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.osimp.OsimAspect;
import org.osimp.ReleaseConnectionAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OsimAutoConfiguration {

    @Bean
    OsimAspect osimAspect(EntityManagerFactory emf) {
        return new OsimAspect(emf);
    }

    @Bean
    ReleaseConnectionAspect releaseConnectionAspect(EntityManagerFactory emf) {
        return new ReleaseConnectionAspect(emf);
    }
}
