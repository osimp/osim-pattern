# Osim - Open Session In Method pattern

A good twin of OSIV — Open Session in Method with no drawbacks.

## Osim annotation

Put `@Osim` on a method to ensure the same `EntityManager` (and persistence context) is reused for all JPA operations inside that method. This is handy when you want to keep one session while using multiple transactions (e.g., calling sub-methods with `REQUIRES_NEW`).

Example (Jakarta, Spring Boot 3.x):

```java
import jakarta.persistence.EntityManager;
import org.osimp.api.Osim;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {
    private final EntityManager em;

    public ProductService(EntityManager em) {
        this.em = em;
    }

    @Osim
    public boolean loadTwiceInSameSession(UUID productId) {
        // Both finds will return the same managed instance within a single session
        var p1 = em.find(Product.class, productId);
        var p2 = em.find(Product.class, productId);
        return p1 == p2; // true with @Osim
    }
}
```

Notes:
- If you are on Spring 5.x / Java EE (javax), just change the import to `javax.persistence.EntityManager` — everything else is the same.
- `@Osim` can be combined with `@Transactional`; inner calls with `REQUIRES_NEW` will still see the same session while having separate transactions.

## ReleaseConnection annotation

Put `@ReleaseConnection` on a method if you want to release the DB connection before the method starts. This is useful to avoid holding a connection while you perform long non-DB operations (e.g., HTTP calls, sleeps, CPU-bound tasks).

Example (Spring Boot, using Hikari):

```java
import com.zaxxer.hikari.HikariDataSource;
import org.osimp.api.ReleaseConnection;
import org.springframework.stereotype.Service;

@Service
public class ExternalServiceClient {
    private final HikariDataSource dataSource;

    public ExternalServiceClient(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @ReleaseConnection
    public int getCurrentActiveConnectionsNumber() {
        // Connection is released before method execution; no DB is held while computing
        return dataSource.getHikariPoolMXBean().getActiveConnections();
    }
}
```

Typical use-cases:
- Wrapping long HTTP calls so the application isn’t keeping DB connections idle.
- Sections of code that do not touch the database but may take time (throttling, retries, backoff, external RPC).



