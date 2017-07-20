package jhipster.reactive.web.rest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.web.server.HeaderBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
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
        return maybeResponse.flatMap(response -> {
            ArrayList<ServerResponse.BodyBuilder> conteneur = new ArrayList<>(1);
            ServerResponse.BodyBuilder ok = ServerResponse.ok();
            conteneur.set(0,ok);
            if (header!=null){
                header.forEach((key, value) ->{
                    ServerResponse.BodyBuilder tmp = conteneur.get(0).header(key,value.get(0));
                    conteneur.set(0,tmp);
                });
            }
            return conteneur.get(0).syncBody(response);
         })
            .switchIfEmpty(ServerResponse.notFound().build());
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
