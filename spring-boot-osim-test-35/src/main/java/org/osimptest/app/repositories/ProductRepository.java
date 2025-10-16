package org.osimptest.app.repositories;

import org.osimptest.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> { }
