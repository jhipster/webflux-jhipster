package jhipster.reactive.web.rest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return Mono.fromCallable(callable).publishOn(scheduler);
    }

    public static <X> Mono<ResponseEntity<X>> wrapOrNotFound(Mono<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, null);
    }

    public static <X> Mono<ResponseEntity<X>> wrapOrNotFound(Mono<X> maybeResponse, HttpHeaders header) {
        return maybeResponse.flatMap(response -> {
            return Mono.just(ResponseEntity.ok().headers(header).body(response));
            }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public static <T> Mono<HttpHeaders> generatePaginationHttpHeaders(Mono<Page<T>> pageMono, String baseUrl) {

        return pageMono.flatMap((Page<T> page)-> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
            String link = "";
            if ((page.getNumber() + 1) < page.getTotalPages()) {
                link = "<" + PaginationUtil.generateUri(baseUrl, page.getNumber() + 1, page.getSize()) + ">; rel=\"next\",";
            }
            // prev link
            if ((page.getNumber()) > 0) {
                link += "<" + PaginationUtil.generateUri(baseUrl, page.getNumber() - 1, page.getSize()) + ">; rel=\"prev\",";
            }
            // last and first link
            int lastPage = 0;
            if (page.getTotalPages() > 0) {
                lastPage = page.getTotalPages() - 1;
            }
            link += "<" + PaginationUtil.generateUri(baseUrl, lastPage, page.getSize()) + ">; rel=\"last\",";
            link += "<" + PaginationUtil.generateUri(baseUrl, 0, page.getSize()) + ">; rel=\"first\"";
            headers.add(HttpHeaders.LINK, link);
            return Mono.just(headers);
        });
    }
}
