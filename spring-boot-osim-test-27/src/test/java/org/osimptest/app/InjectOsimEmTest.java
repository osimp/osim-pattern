package org.osimptest.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.osimp.OsimAspect;
import org.osimptest.app.entities.Product;
import org.osimptest.app.repositories.ProductRepository;
import org.osimptest.app.services.OsimEmHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InjectOsimEmTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OsimEmHolderService osimEmHolder;

    private Product savedProduct;

    @BeforeAll
    void setUp() {
        Product product = new Product();
        product.setName("phone");
        product.setPrice(345);

        savedProduct = productRepository.save(product);
    }

    @Autowired
    OsimAspect osimAspect;

    @Test
    void osimAspectCreatedSuccessfully() {
        assertThat(osimAspect).isNotNull();
    }

    @Test
    void osimCreatesSharedEntityManager() {
        boolean found = osimEmHolder.emFoundOsim(savedProduct.getId());
        assertTrue(found);
    }

    @Test
    void entityManagerIsNotSharedByDefault() {
        boolean found = osimEmHolder.emFoundPlain(savedProduct.getId());
        assertFalse(found);
    }
}
