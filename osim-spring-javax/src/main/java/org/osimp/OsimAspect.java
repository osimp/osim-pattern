package org.osimp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

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

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            EntityManagerHolder emHolder = new EntityManagerHolder(em);
            TransactionSynchronizationManager.bindResource(emf, emHolder);
            return pjp.proceed();
        } catch (PersistenceException ex) {
            throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
        } finally {
            if (em != null) {
                Exception ex = null;
                try {
                    TransactionSynchronizationManager.unbindResourceIfPossible(emf);
                } catch (Exception e) {
                    ex = e;
                }
                try {
                    em.close();
                } catch (Exception e) {
                    if (ex != null) {
                        e.addSuppressed(ex);
                        throw e;
                    }
                }
                if (ex != null) {
                    throw ex;
                }
            }
        }
    }

}
