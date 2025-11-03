package org.osimp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
public class OsimAspect {

    private final EntityManagerFactory emf;

    public OsimAspect(
            EntityManagerFactory emf
    ) {
        this.emf = emf;
    }

    @Around("@annotation(org.osimp.api.Osim)")
    public Object performOsimly(ProceedingJoinPoint pjp) throws Throwable {
        if (TransactionSynchronizationManager.hasResource(emf)) {
            return pjp.proceed();
        }

        EntityManager em;
        try {
            em = emf.createEntityManager();
        } catch (PersistenceException ex) {
            throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
        }
        try (em) {
            EntityManagerHolder emHolder = new EntityManagerHolder(em);
            TransactionSynchronizationManager.bindResource(emf, emHolder);
            return pjp.proceed();
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(emf);
        }
    }

}
