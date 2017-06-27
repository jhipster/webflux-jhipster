package jhipster.reactive.web.rest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@Component
public class AsyncUtil {

    private Scheduler scheduler;

    public AsyncUtil(@Value("${jhipster.async.max-pool-size}")Integer availableThreads) {
        this.scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(availableThreads));
    }

    public <T> Mono<T> async(Callable<T> callable) {
        return Mono.
            fromCallable(callable).
            publishOn(
                scheduler);
    }

}
