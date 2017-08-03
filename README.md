# Reactive JHipster study project

Each module is a Jhipster app with Cassandra:
- defaultSpring4 is the app by default, in Spring 4.
- defaultSpring5 is the app migrated to Spring 5.
- reactiveCassandra is defaultSpring5 with the full REST layer in reactive mode.
- reporeactiveMongoDB is defaultSpring5 with the REST Controller classes for the entities reactive all the way down to the repositories. The other controllers are not reactive.

The results of the Gatling tests can be found at the root of each module.

Our article : http://blog.ippon.tech/spring-5-webflux-performance-tests/
