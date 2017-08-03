# Reactive JHipster study project

Each module is a Jhipster app:
- defaultSpring4 is the app by default, in Spring 4.
- defaultSpring5 is the app migrated to Spring 5.
- onlyOperationReactive is defaultSpring5 with the REST Controller class of an entity (Operation.java) in reactive mode.
- onlyOperationReporeactive is defaultSpring5 with an entity (Operation.java) in reactive mode (from the reactive repository to the REST Controller Class).
- reactiveMongoDB is defaultSpring5 with the full REST layer in reactive mode.
- reporeactiveMongoDB is defaultSpring5 with the REST Controller classes for the entities reactive all the way down to the repositories, and with the other classes of the REST layer reactive only on the REST level.

The results of the Gatling tests can be found at the root of each module.

Our article : http://blog.ippon.tech/spring-5-webflux-performance-tests/
