package jhipster.reactive.web.rest;


import com.codahale.metrics.annotation.Timed;
import jhipster.reactive.domain.BankAccount;
import jhipster.reactive.repository.BankAccountRepository;
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
 * REST controller for managing BankAccount.
 */
@RestController
@RequestMapping("/api")
public class BankAccountResource {

    private final Logger log = LoggerFactory.getLogger(BankAccountResource.class);

    private static final String ENTITY_NAME = "bankAccount";

    private final BankAccountRepository bankAccountRepository;

    public BankAccountResource(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * POST  /bank-accounts : Create a new bankAccount.
     *
     * @param bankAccount the bankAccount to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bankAccount, or with status 400 (Bad Request) if the bankAccount has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect //TODO: think about it
     */
    @PostMapping("/bank-accounts")
    @Timed
    public Mono<ResponseEntity<BankAccount>> createBankAccount(@Valid @RequestBody BankAccount bankAccount) {
        log.debug("REST request to save BankAccount : {}", bankAccount);
        if (bankAccount.getId() != null) {
            return Mono.just(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new bankAccount cannot already have an ID")).body(null));
        }
        Mono<BankAccount> result = bankAccountRepository.save(bankAccount);
        return result.map(savedBankAccount ->
            ResponseEntity.created(URI.create("/api/bank-accounts/" + savedBankAccount.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, savedBankAccount.getId()))
                .body(savedBankAccount));
    }

    /**
     * PUT  /bank-accounts : Updates an existing bankAccount.
     *
     * @param bankAccount the bankAccount to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bankAccount,
     * or with status 400 (Bad Request) if the bankAccount is not valid,
     * or with status 500 (Internal Server Error) if the bankAccount couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bank-accounts")
    @Timed
    public Mono<ResponseEntity<BankAccount>> updateBankAccount(@Valid @RequestBody BankAccount bankAccount) throws URISyntaxException {
        log.debug("REST request to update BankAccount : {}", bankAccount);
        if (bankAccount.getId() == null) {
            return createBankAccount(bankAccount);
        }
        Mono<BankAccount> result = bankAccountRepository.save(bankAccount);
        return result.map((BankAccount savedBankAccount) ->
            ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, savedBankAccount.getId()))
                .body(savedBankAccount));
    }

    /**
     * GET  /bank-accounts : get all the bankAccounts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of bankAccounts in body
     */
    @GetMapping("/bank-accounts")
    @Timed
    public Flux<BankAccount> getAllBankAccounts() {
        log.debug("REST request to get all BankAccounts");
        return bankAccountRepository.findAll();
    }

    /**
     * GET  /bank-accounts/:id : get the "id" bankAccount.
     *
     * @param id the id of the bankAccount to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bankAccount, or with status 404 (Not Found)
     */
    @GetMapping("/bank-accounts/{id}")
    @Timed
    public Mono<ResponseEntity<BankAccount>> getBankAccount(@PathVariable String id) {
        log.debug("REST request to get BankAccount : {}", id);
        Mono<BankAccount> bankAccount = bankAccountRepository.findById(id);
        return AsyncUtil.wrapOrNotFound(bankAccount);
    }

    /**
     * DELETE  /bank-accounts/:id : delete the "id" bankAccount.
     *
     * @param id the id of the bankAccount to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bank-accounts/{id}")
    @Timed
    public Mono<ResponseEntity<Void>> deleteBankAccount(@PathVariable String id) {
        log.debug("REST request to delete BankAccount : {}", id);
        return bankAccountRepository.findById(id)
            .map(savedBankAccount -> {
                bankAccountRepository.deleteById(id).subscribe();
                return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
            });
    }
}
