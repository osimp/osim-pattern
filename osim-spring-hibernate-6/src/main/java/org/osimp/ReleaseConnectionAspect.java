package org.osimp;

import jakarta.persistence.EntityManagerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
public class ReleaseConnectionAspect {

    private final EntityManagerFactory emf;

    public ReleaseConnectionAspect(
            EntityManagerFactory emf
    ) {
        this.emf = emf;
    }

    @Around("@annotation(org.osimp.api.ReleaseConnection)")
    public Object performReleased(ProceedingJoinPoint pjp) throws Throwable {
        EntityManagerHolder em = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
        if (em == null) {
            return pjp.proceed();
        }
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            return pjp.proceed();
        }
        em.getEntityManager()
                .unwrap(SessionImplementor.class)
                .getJdbcCoordinator()
                .getLogicalConnection()
                .manualDisconnect();
        return pjp.proceed();
    }
}
