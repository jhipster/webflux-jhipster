# Reactive JHipster study project
Each module is a Jhipster app:
- defaultSpring4 is the app by default, in Spring 4.
- defaultSpring5 is the app migrated to Spring 5.
- onlyOperationReactiveMySql is defaultSpring5 with the REST Controller Class of an entity (Operation.java) in reactive mode.
- reactiveMySql is defaultSpring5 with the full REST layer in reactive mode.

As MySQL has no reactive driver, we launch methods with a thread-pool by using Mono.fromCallable(callable).publishOn(scheduler) to make it reactive (see src/main/java/jhipster/reactive/web/rest/util/AsyncUtil.java).
