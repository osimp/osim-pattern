package org.osimptest.app.services.impl;

import org.osimp.api.Osim;
import org.osimptest.app.entities.Product;
import org.osimptest.app.repositories.ProductRepository;
import org.osimptest.app.services.OsimEmHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.UUID;

@Component
public class OsimEmHolderServiceImpl implements OsimEmHolderService {

    @Autowired
    EntityManager em;

    private final ProductRepository productRepository;

    public OsimEmHolderServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Osim
    @Override
    public boolean emFoundOsim(UUID productId) {
//        EntityManagerHolder emh = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
        Product p1 = em.find(Product.class, productId);
        Product p2 = em.find(Product.class, productId);
        return p1 == p2;
    }

    @Override
    public boolean emFoundPlain(UUID productId) {
        Product p1 = em.find(Product.class, productId);
        Product p2 = em.find(Product.class, productId);
        return p1 == p2;
//        return TransactionSynchronizationManager.hasResource(emf);
    }
}
