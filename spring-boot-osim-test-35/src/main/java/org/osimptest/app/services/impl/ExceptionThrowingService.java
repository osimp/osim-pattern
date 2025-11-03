package org.osimptest.app.services.impl;

import org.osimp.api.Osim;
import org.osimptest.app.services.LongOperationService;
import org.springframework.stereotype.Service;

@Service("exceptionThrowingService")
public class ExceptionThrowingService implements LongOperationService {

    @Override
    @Osim
    public void throwException(RuntimeException e) {
        throw e;
    }
}
