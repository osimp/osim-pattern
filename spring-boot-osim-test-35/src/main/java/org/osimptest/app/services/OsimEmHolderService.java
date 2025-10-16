package org.osimptest.app.services;

import java.util.UUID;

public interface OsimEmHolderService {
    boolean emFoundOsim(UUID productId);

    boolean emFoundPlain(UUID productId);
}
