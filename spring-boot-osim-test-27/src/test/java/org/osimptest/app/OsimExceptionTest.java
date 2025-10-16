package org.osimptest.app;

import org.junit.jupiter.api.Test;
import org.osimp.OsimAspect;
import org.osimptest.app.entities.Product;
import org.osimptest.app.repositories.ProductRepository;
import org.osimptest.app.services.LongOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
class OsimExceptionTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    LongOperationService exceptionThrowingService;

    private Product savedProduct;

    @PostConstruct
    void setUp() {
        Product product = new Product();
        product.setName("phone");
        product.setPrice(345);

        savedProduct = productRepository.save(product);
    }

    @Autowired
    OsimAspect osimAspect;

    @Autowired
    EntityManagerFactory emf;

    @Test
    void updateProductPrice() {
        assertThat(osimAspect).isNotNull();

//        assertThrow() -> exceptionThrowingService.updateProductPrice(null))
        assertThatThrownBy(() -> exceptionThrowingService.updateProductPrice(null))
                .hasMessage("Planned exception");

        assertThat(TransactionSynchronizationManager.hasResource(emf)).isFalse();
    }
}
