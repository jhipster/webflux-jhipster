package jhipster.reactive.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiParam;
import jhipster.reactive.domain.Operation;
import jhipster.reactive.repository.OperationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URISyntaxException;

/**
 * REST controller for managing Operation.
 */
@RestController
@RequestMapping("/api")
public class OperationResource {

    private final Logger log = LoggerFactory.getLogger(OperationResource.class);

    private static final String ENTITY_NAME = "operation";

    private final OperationRepository operationRepository;

    public OperationResource(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    /**
     * POST  /operations : Create a new operation.
     *
     * @param operation the operation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new operation, or with status 400 (Bad Request) if the operation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/operations")
    @Timed
    public Mono<Operation> createOperation(@Valid @RequestBody Operation operation) throws URISyntaxException {
        log.debug("REST request to save Operation : {}", operation);
        if (operation.getId() != null) {
            return Mono.empty();
        }
        return operationRepository.save(operation);
    }

    /**
     * PUT  /operations : Updates an existing operation.
     *
     * @param operation the operation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated operation,
     * or with status 400 (Bad Request) if the operation is not valid,
     * or with status 500 (Internal Server Error) if the operation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/operations")
    @Timed
    public Mono<Operation> updateOperation(@Valid @RequestBody Operation operation) throws URISyntaxException {
        log.debug("REST request to update Operation : {}", operation);
        if (operation.getId() == null) {
            return createOperation(operation);
        }
        Mono<Operation> result = operationRepository.save(operation);
        return result.map((Operation savedOperation) -> operation);
    }


    /**
     * GET  /operations : get all the operations.
     * Two calls to the database are performed.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of operations in body
     */
    @GetMapping("/operations")
    @Timed
    public Flux<Operation> getAllOperations(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Operations");
        return operationRepository.findAllBy(pageable);
    }

    /**
     * GET  /operations/:id : get the "id" operation.
     *
     * @param id the id of the operation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the operation, or with status 404 (Not Found)
     */
    @GetMapping("/operations/{id}")
    @Timed
    public Mono<Operation> getOperation(@PathVariable String id) {
        log.debug("REST request to get Operation : {}", id);
        Mono<Operation> operation = operationRepository.findById(id);
        return operation;
    }

    /**
     * DELETE  /operations/:id : delete the "id" operation.
     *
     * @param id the id of the operation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/operations/{id}")
    @Timed
    public Mono<Void> deleteOperation(@PathVariable String id) {
        log.debug("REST request to delete Operation : {}", id);
        return operationRepository.findById(id)
            .flatMap(savedOperation -> {
                operationRepository.deleteById(id).subscribe();
                return Mono.empty();
            });
    }
}
