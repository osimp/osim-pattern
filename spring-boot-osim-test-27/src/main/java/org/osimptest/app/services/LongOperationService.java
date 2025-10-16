package org.osimptest.app.services;

import java.util.UUID;

public interface LongOperationService {
    void updateProductPrice(UUID productId);
}
