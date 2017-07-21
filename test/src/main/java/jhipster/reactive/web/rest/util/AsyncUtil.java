package jhipster.reactive.web.rest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Component
public class AsyncUtil {

    private Scheduler scheduler;

    public AsyncUtil(@Value("${jhipster.async.max-pool-size}")Integer availableThreads) {
        //this.scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(availableThreads));
//        this.scheduler = Schedulers.elastic();
        this.scheduler = Schedulers.elastic();
    }


    /* === MONO part === */

    public <T> Mono<T> asyncMono(Callable<T> callable) {
        return Mono.fromCallable(callable).publishOn(scheduler);
    }

    public static <X> Mono<ServerResponse> wrapOrNotFound(Mono<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, null);
    }

    public static <X> Mono<ServerResponse> wrapOrNotFound(Mono<X> maybeResponse, HttpHeaders header) {
        return maybeResponse
            .flatMap(response -> ServerResponse.ok().headers((headers) -> headers.addAll(header)).syncBody(response))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public static ServerResponse.BodyBuilder header(ServerResponse.BodyBuilder bodyBuilder, HttpHeaders httpHeaders){
        ServerResponse.BodyBuilder myBodyBuilder = bodyBuilder;
        Iterator<Map.Entry<String, List<String>>> iterator = httpHeaders.entrySet().iterator();
        Map.Entry<String, List<String>> entry = null;
        while (iterator.hasNext()){
            entry = iterator.next();
            myBodyBuilder = myBodyBuilder.header(entry.getKey(), entry.getValue().get(0));
        }
        return myBodyBuilder;
    }
    /* === FLUX part === */

    public <T> Flux<T> asyncFlux(List<T> list) {
        if(list==null) return null;
        return Flux.fromStream(list.stream()).publishOn(scheduler);
    }

    public <T> Flux<T> asyncFlux(Stream<T> stream) {
        if(stream==null) return null;
        return Flux.fromStream(stream).publishOn(scheduler);
    }

    public <T> Flux<T> asyncFlux(Iterable<T> iterable) {
        if(iterable==null) return null;
        return Flux.fromIterable(iterable).publishOn(scheduler);
    }

    public <T> Flux<T> asyncFlux(T[] array) {
        if(array==null) return null;
        return Flux.fromArray(array).publishOn(scheduler);
    }

}
