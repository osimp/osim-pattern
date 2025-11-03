package org.osimptest.app;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.osimp.OsimAspect;
import org.osimptest.app.services.LongOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
class OsimExceptionWhileCreatingEntityManagerTest {

    @Autowired
    LongOperationService exceptionThrowingService;

    @Autowired
    OsimAspect osimAspect;

    @MockitoSpyBean
    EntityManagerFactory emf;

    @Test
    void persistenceExceptionWhileCreatingEntityManager() {
        doThrow(new PersistenceException("Exception while creating EntityManager for Osim marked method"))
                .when(emf).createEntityManager();

        assertThat(osimAspect).isNotNull();

        assertThatThrownBy(() -> exceptionThrowingService.throwException(
                new PersistenceException("This exception is not going to be thrown")))
                .isInstanceOf(DataAccessResourceFailureException.class)
        ;

        assertThat(TransactionSynchronizationManager.hasResource(emf)).isFalse();
    }
}
