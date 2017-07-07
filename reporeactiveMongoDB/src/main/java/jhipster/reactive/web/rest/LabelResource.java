package jhipster.reactive.web.rest;

import com.codahale.metrics.annotation.Timed;
import jhipster.reactive.domain.Label;
import jhipster.reactive.repository.LabelRepository;
import jhipster.reactive.web.rest.util.AsyncUtil;
import jhipster.reactive.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing Label.
 */
@RestController
@RequestMapping("/api")
public class LabelResource {

    private final Logger log = LoggerFactory.getLogger(LabelResource.class);

    private static final String ENTITY_NAME = "label";

    private final LabelRepository labelRepository;

    public LabelResource(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    /**
     * POST  /labels : Create a new label.
     *
     * @param label the label to create
     * @return the ResponseEntity with status 201 (Created) and with body the new label, or with status 400 (Bad Request) if the label has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/labels")
    @Timed
    public Mono<ResponseEntity<Label>> createLabel(@Valid @RequestBody Label label) {
        log.debug("REST request to save Label : {}", label);
        if (label.getId() != null) {
            return Mono.just(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new label cannot already have an ID")).body(null));
        }
        Mono<Label> result = labelRepository.save(label);
        return result.map((Label savedLabel) ->
            ResponseEntity.created(URI.create("/api/labels/" + savedLabel.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, savedLabel.getId()))
                .body(savedLabel));
    }

    /**
     * PUT  /labels : Updates an existing label.
     *
     * @param label the label to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated label,
     * or with status 400 (Bad Request) if the label is not valid,
     * or with status 500 (Internal Server Error) if the label couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/labels")
    @Timed
    public Mono<ResponseEntity<Label>> updateLabel(@Valid @RequestBody Label label) throws URISyntaxException {
        log.debug("REST request to update Label : {}", label);
        if (label.getId() == null) {
            return createLabel(label);
        }
        Mono<Label> result = labelRepository.save(label);
        return result.map((Label savedLabel) ->
            ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, savedLabel.getId()))
                .body(savedLabel)
        );

    }

    /**
     * GET  /labels : get all the labels.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of labels in body
     */
    @GetMapping("/labels")
    @Timed
    public Flux<Label> getAllLabels() {
        log.debug("REST request to get all Labels");
        return labelRepository.findAll();
    }

    /**
     * GET  /labels/:id : get the "id" label.
     *
     * @param id the id of the label to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the label, or with status 404 (Not Found)
     */
    @GetMapping("/labels/{id}")
    @Timed
    public Mono<ResponseEntity<Label>> getLabel(@PathVariable String id) {
        log.debug("REST request to get Label : {}", id);
        Mono<Label> label = labelRepository.findById(id);
        return AsyncUtil.wrapOrNotFound(label);
    }

    /**
     * DELETE  /labels/:id : delete the "id" label.
     *
     * @param id the id of the label to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/labels/{id}")
    @Timed
    public Mono<ResponseEntity<Void>> deleteLabel(@PathVariable String id) {
        log.debug("REST request to delete Label : {}", id);
        return labelRepository.findById(id).map(savedLabel -> {
            labelRepository.deleteById(id).subscribe();
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
        });
    }
}
