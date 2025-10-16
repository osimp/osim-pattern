package org.osimptest.app.services.impl;

import org.osimp.api.Osim;
import org.osimptest.app.services.LongOperationService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("exceptionThrowingService")
public class ExceptionThrowingService implements LongOperationService {

    @Override
    @Osim
    public void updateProductPrice(UUID productId) {
        throw new RuntimeException("Planned exception");
    }
}
