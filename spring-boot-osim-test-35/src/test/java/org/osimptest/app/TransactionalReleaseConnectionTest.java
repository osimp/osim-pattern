package org.osimptest.app;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.osimptest.app.entities.Product;
import org.osimptest.app.repositories.ProductRepository;
import org.osimptest.app.services.ExternalServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionalReleaseConnectionTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ExternalServiceClient esc;

    @Autowired
    SessionFactory emf;

    @Autowired
    HikariDataSource hds;

//    @Autowired
//    Session em;

    private Product savedProduct;

    @BeforeAll
    void setUp() {
        Product product = new Product();
        product.setName("sim");
        product.setPrice(348);

        savedProduct = productRepository.save(product);
    }

    /**
     * This test ensures, that EnsureConnectionReleased will not release
     * connection if transaction is in progress.
     * <p/>
     * Contrary to intuitive thinking, you can release connection in the
     * middle of Hibernate transaction and Hibernate will let you do that.
     * <p/>
     * Connection will be released and current transaction will be rolled
     * back.
     * <p/>
     * The fact, that connection wasn't released is tested in two ways.
     * <p/>
     * <ol>
     * <li>If you release connection in the middle of transaction, then
     * transaction is rolled back and changes are lost. So the test checks,
     * that changes are not lost.</li>
     * <li>Number of active connections should be 0, when method,
     * annotated by ReleaseConnection is run, unless the connection
     * wasn't released. So the test checks, that no connections are
     * reserved when the method is run.</li>
     * </ol>
     */
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    void connectionsIsNotReleasedIfTransactionIsInProgress() {
        Product product = new Product();
        product.setName("phone");
        product.setPrice(345);

        EntityManagerHolder emh = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
        Product savedProduct = productRepository.save(product);
        productRepository.flush();
        emh.getEntityManager().clear();

        int currentActiveConnectionsNumber = esc.getCurrentActiveConnectionsNumber();
        assertThat(currentActiveConnectionsNumber).isEqualTo(1);

        Optional<Product> loadedProduct = productRepository.findById(savedProduct.getId());
        assertThat(loadedProduct.isPresent()).isTrue();
    }

    @Test
    void connectionIsReleased() {
        try (Session em = (Session) emf.createEntityManager()) {
            EntityManagerHolder emh = new EntityManagerHolder(em);
            TransactionSynchronizationManager.bindResource(emf, emh);
            PhysicalConnectionHandlingMode connectionHandlingMode = em.unwrap(SessionImplementor.class)
                    .getJdbcCoordinator()
                    .getLogicalConnection()
                    .getConnectionHandlingMode();
            assertThat(connectionHandlingMode).isSameAs(PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_HOLD);

            int acInit = hds.getHikariPoolMXBean().getActiveConnections();
            assertThat(acInit).isEqualTo(0);

            em.find(Product.class, savedProduct.getId());
            int acAfterSelect = hds.getHikariPoolMXBean().getActiveConnections();
            assertThat(acAfterSelect).isEqualTo(1);

            int acInsideReleaseConnection = esc.getCurrentActiveConnectionsNumber();
            assertThat(acInsideReleaseConnection).isEqualTo(0);
            int acAfterReleaseConnection = hds.getHikariPoolMXBean().getActiveConnections();
            assertThat(acAfterReleaseConnection).isEqualTo(0);
        } finally {
            TransactionSynchronizationManager.unbindResource(emf);
        }
    }
}
