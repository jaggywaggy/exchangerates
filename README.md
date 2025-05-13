This is a spring boot application that fetches exchange rates from multiple APIs and averages them for the purpose of retrieving currency conversion rates.

Features
    - Fetches live exchange rates from Frankfurter.app and fawazahmed0/currency-api.
    - Averages the returned rate results.
    - Caches our query and response in a concurrent hashmap.
    - Records and exposes usage metrics (/metrics).

Requires Java 17+ and gradle.

Design decisions
    - Used an in memory cache with a concurrent hashmap for simplicity. Ideal for the scope of this assignment.
    - Implemented custom metrics tracking instead of using a library, to match the expected JSON response shape.
    - HTTP calls using RestTemplate for simplicity.

Tests
    - Business logic (ExchangeRateService, MetricsService).
    - Query validation.

Improvements
    - Add controller and integration tests. (`@WebMvcTest`)
    - Implement fallback providers in case one API is down
    - Add some form of cache refreshing for up to date rates.
    - Use proper libraries for caching and metrics.
    - Improve code comments