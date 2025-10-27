# OSIM — Open Session In Method

OSIM is a lean alternative to OSIV (Open Session In View). Instead of keeping a
JPA `EntityManager` open for the whole web request, OSIM narrows the scope down to a
single annotated method. This avoids the typical OSIV drawbacks while still giving
you one consistent persistence context for all JPA operations performed inside that
method.

The library also provides `@ReleaseConnection` to temporarily free a JDBC connection
while your code waits on slow I/O (e.g., HTTP calls). This preserves your pool
capacity under load.

---

## Why OSIM?

- One method, one persistence context: the same `EntityManager` is reused for all
  JPA work within the annotated method.
- No surprise extra SELECTs just to re-fill a fresh persistence context between
  calls.
- Much smaller scope than OSIV: you decide exactly where you need an open session.

## Why ReleaseConnection?

Long-running I/O inside a transactional method can keep a JDBC connection checked
out and idle. With `@ReleaseConnection` the connection is released before the
method starts and re-acquired afterwards so that other requests can use it in the
meantime.

---

## Usage examples

### 1) Keep one EntityManager for the whole method with `@Osim`

```java
import org.osimp.api.Osim;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ExternalServiceClient httpClient;

    public ProductService(ProductRepository productRepository, ExternalServiceClient httpClient) {
        this.productRepository = productRepository;
        this.httpClient = httpClient;
    }

    @Osim
    public void updateProductPrice(UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow();

        int newPrice = httpClient.getNewPrice(
            product.getExternalProductId()
        );

        product.setPrice(newPrice);
        productRepository.save(product);
    }
}
```

- `@Osim` ensures a single `EntityManager` is used across all JPA operations in
  `updateProductPrice(...)`.
- This prevents additional SELECTs that could appear when multiple separate
  persistence contexts are involved within the same logical operation.

### 2) Free the DB connection during slow I/O with `@ReleaseConnection`

```java
import org.osimp.api.ReleaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ExternalServiceClientImpl implements ExternalServiceClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalServiceClientImpl.class);

    @Override
    @ReleaseConnection
    public int getNewPrice(UUID externalProductId) {
        // Example: while we wait for an HTTP call, the JDBC connection is released
        // back to the pool, so other transactions can proceed.
        log.info("Fetching price for {} ...", externalProductId);
        // ... perform HTTP call here ...
        return 36; // demo value
    }
}
```

- When `getNewPrice(...)` runs, the JDBC connection is not held.
- Especially useful when the caller is within an OSIM or transactional method and
  the callee performs slow network I/O.

---

## Quick start

Pick the starter that matches your Spring Boot generation.

- Spring Boot 2.7.x (javax):

```xml
<dependency>
  <groupId>org.osimp</groupId>
  <artifactId>spring-boot-starter-osim-27</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

- Spring Boot 3.x (jakarta):

```xml
<dependency>
  <groupId>org.osimp</groupId>
  <artifactId>spring-boot-starter-osim-35</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Then use the annotations from `org.osimp.api`:

```java
import org.osimp.api.Osim;
import org.osimp.api.ReleaseConnection;
```

No additional configuration is required — the starters include auto-configuration
(`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`).

---

## How it works (high level)

- `@Osim` wraps the target method with an aspect that binds a single
  `EntityManager` to the calling thread for the duration of the method. All JPA
  operations inside reuse it.
- `@ReleaseConnection` wraps the target method so that the underlying Hibernate /
  JPA session temporarily releases the JDBC connection while the method executes,
  reacquiring it afterwards.

---

## Notes and caveats

- Standard Spring AOP rules apply: annotations are effective when methods are
  invoked through Spring proxies (typically public methods on Spring-managed beans).
- Avoid doing long blocking work inside `@Osim` methods unless you also mark the
  blocking segment with `@ReleaseConnection`.
- Works both with and without explicit `@Transactional`; choose what fits your
  consistency needs.

---

## Modules overview

- `osim-pattern-api`: annotations (`@Osim`, `@ReleaseConnection`).
- `osim-spring-javax` / `osim-spring-jakarta`: OSIM aspects for Spring (javax vs
  jakarta).
- `osim-spring-hibernate-5` / `osim-spring-hibernate-6`: `@ReleaseConnection`
  aspect implementations for Hibernate 5.x vs 6.x.
- Starters `spring-boot-starter-osim-27` and `spring-boot-starter-osim-35` bring
  everything together for their respective Spring Boot lines.



