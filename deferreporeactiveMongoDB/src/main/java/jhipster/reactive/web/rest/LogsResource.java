package jhipster.reactive.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import jhipster.reactive.web.rest.util.AsyncUtil;
import jhipster.reactive.web.rest.vm.LoggerVM;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/management")
public class LogsResource {

    @Autowired
    private AsyncUtil asyncUtil;

    @GetMapping("/logs")
    @Timed
    public Mono<List<LoggerVM>> getList() {
        return asyncUtil.asyncMono(() -> {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            return context.getLoggerList()
                .stream()
                .map(LoggerVM::new)
                .collect(Collectors.toList());
        });
    }

    @PutMapping("/logs")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void changeLevel(@RequestBody LoggerVM jsonLogger) {
        asyncUtil.asyncMono(() -> {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
            return null;
        });
    }
}
