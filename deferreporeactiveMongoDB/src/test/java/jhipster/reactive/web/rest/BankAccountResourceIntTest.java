package jhipster.reactive.web.rest;

import jhipster.reactive.ReporeactiveMongoDbApp;
import jhipster.reactive.domain.BankAccount;
import jhipster.reactive.repository.BankAccountRepository;
import jhipster.reactive.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BankAccountResource REST controller.
 *
 * @see BankAccountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReporeactiveMongoDbApp.class)
public class BankAccountResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE = new BigDecimal(2);

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private WebTestClient client;

    private BankAccount bankAccount;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BankAccountResource bankAccountResource = new BankAccountResource(bankAccountRepository);
        this.client = WebTestClient
            .bindToController(bankAccountResource)
            .build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankAccount createEntity() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName(DEFAULT_NAME);
        bankAccount.setBalance(DEFAULT_BALANCE);
        return bankAccount;
    }

    @Before
    public void initTest() {
        bankAccountRepository.deleteAll();
        bankAccount = createEntity();
    }

    @Test
    public void createBankAccount() throws Exception {
        Mono<Integer> databaseSizeBeforeCreate = bankAccountRepository.findAll().collectList().map((List list)->list.size());

        // Create the BankAccount
        this.client.put().uri("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .syncBody(TestUtil.convertObjectToJsonBytes(bankAccount))
            .exchange()
            .expectStatus().isCreated();

        // Validate the BankAccount in the database
        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list)->list.size()),databaseSizeBeforeCreate)
            .map((Tuple2 tuple)->{
                assertTrue(tuple.getT1().equals((Integer)tuple.getT2()+1));
                return null;
            }).subscribe();

        Mono<BankAccount> testBankAccount = bankAccountList.last();
        testBankAccount.map((BankAccount lastBankAccount)->{
            assertThat(lastBankAccount.getName()).isEqualTo(DEFAULT_NAME);
            assertThat(lastBankAccount.getBalance()).isEqualTo(DEFAULT_BALANCE);
            return null;
        }).subscribe();

    }

    @Test
    public void createBankAccountWithExistingId() throws Exception {
        Mono<Integer> databaseSizeBeforeCreate = bankAccountRepository.findAll().collectList().map((List list)->list.size());

        // Create the BankAccount with an existing ID
        bankAccount.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        this.client.post().uri("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .syncBody(TestUtil.convertObjectToJsonBytes(bankAccount))
            .exchange()
            .expectStatus().isBadRequest();

        // Validate the Alice in the database
        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list)->list.size()),databaseSizeBeforeCreate)
            .map((Tuple2 tuple)->{
                assertTrue(tuple.getT1().equals(tuple.getT2()));
                return null;
            }).subscribe();
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        Mono<Integer> databaseSizeBeforeTest = bankAccountRepository.findAll().collectList().map((List list)->list.size());
        // set the field null
        bankAccount.setName(null);

        // Create the BankAccount, which fails.

        this.client.post().uri("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .syncBody(TestUtil.convertObjectToJsonBytes(bankAccount))
            .exchange()
            .expectStatus().isBadRequest();

        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list)->list.size()),databaseSizeBeforeTest)
            .map((Tuple2 tuple)->{
                assertTrue(tuple.getT1().equals(tuple.getT2()));
                return null;
            }).subscribe();
    }

    @Test
    public void checkBalanceIsRequired() throws Exception {
        Mono<Integer> databaseSizeBeforeTest = bankAccountRepository.findAll().collectList().map((List list)->list.size());
        // set the field null
        bankAccount.setBalance(null);

        // Create the BankAccount, which fails.
        this.client.post().uri("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .syncBody(TestUtil.convertObjectToJsonBytes(bankAccount))
            .exchange()
            .expectStatus().isBadRequest();

        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list)->list.size()),databaseSizeBeforeTest)
            .map((Tuple2 tuple)->{
                assertTrue(tuple.getT1().equals(tuple.getT2()));
                return null;
            }).subscribe();
    }

    @Test
    public void getAllBankAccounts() throws Exception {
        // Initialize the database
        bankAccountRepository.deleteAll().block();
        bankAccountRepository.save(bankAccount).subscribe();
        ArrayList<BankAccount> bankAccountArrayList = new ArrayList<>(1);
        bankAccountArrayList.add(bankAccount);

        // Get all the bankAccountList
        this.client.get().uri("/api/bank-accounts?sort=id,desc")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(TestUtil.APPLICATION_JSON_UTF8)
            .expectBodyList(BankAccount.class)
            .isEqualTo(bankAccountArrayList);
    }

    @Test
    public void getBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.deleteAll().block();
        bankAccountRepository.save(bankAccount).subscribe();
        ArrayList<BankAccount> bankAccountArrayList = new ArrayList<>(1);
        bankAccountArrayList.add(bankAccount);

        // Get the bankAccount
        this.client.get().uri("/api/bank-accounts/{id}", bankAccount.getId())
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(TestUtil.APPLICATION_JSON_UTF8)
            .expectBodyList(BankAccount.class)
            .isEqualTo(bankAccountArrayList);
    }

    @Test
    public void getNonExistingBankAccount() throws Exception {
        // Get the bankAccount
        this.client.get().uri("/api/bank-accounts/{id}", Long.MAX_VALUE)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void updateBankAccount() throws Exception {
        // Initialize the database
        Mono<BankAccount> updatedBankAccount = bankAccountRepository.save(bankAccount);
        updatedBankAccount.subscribe();
        Mono<Integer> databaseSizeBeforeUpdate = bankAccountRepository.findAll().collectList().map((List list)->list.size());

        // Update the bankAccount
        updatedBankAccount.hasElement()
            .map((Boolean bool) -> {
                assertTrue(bool);
                return null;
            })
            .subscribe();
        updatedBankAccount.map((BankAccount savedBankAccount)->{
            savedBankAccount.setId(null);
            savedBankAccount.setName(UPDATED_NAME);
            savedBankAccount.setBalance(UPDATED_BALANCE);
            try{
                this.client.put().uri("/api/bank-accounts")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .syncBody(TestUtil.convertObjectToJsonBytes(savedBankAccount))
                    .exchange()
                    .expectStatus().isCreated();
            } catch (Exception e){
                throw new RuntimeException();
            }
            return null;
        }).subscribe();

        // Validate the BankAccount in the database
        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list)->list.size()),databaseSizeBeforeUpdate)
            .map((Tuple2 tuple)->{
                assertTrue(tuple.getT1().equals(tuple.getT2()));
                return null;
            })
            .subscribe();

        Mono<BankAccount> testBankAccount = bankAccountList.last();
        testBankAccount.map((BankAccount lastBankAccount)->{
            assertThat(lastBankAccount.getName()).isEqualTo(UPDATED_NAME);
            assertThat(lastBankAccount.getBalance()).isEqualTo(UPDATED_BALANCE);
            return null;
        }).subscribe();
    }

    @Test
    public void updateNonExistingBankAccount() throws Exception {
        Mono<Integer> databaseSizeBeforeUpdate = bankAccountRepository.findAll().collectList().map((List list)->list.size());
        // Create the BankAccount
        // If the entity doesn't have an ID, it will be created instead of just being updated
        this.client.put().uri("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .syncBody(TestUtil.convertObjectToJsonBytes(bankAccount))
            .exchange()
            .expectStatus().isCreated();

        // Validate the BankAccount in the database
        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list) -> list.size()), databaseSizeBeforeUpdate)
            .flatMap((Tuple2 tuple) -> {
                assertTrue(tuple.getT1().equals((Integer) tuple.getT2() + 1));
                return null;
            })
            .subscribe();

    }

    @Test
    public void deleteBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.deleteAll().block();
        bankAccountRepository.save(bankAccount).subscribe();
        Mono<Integer> databaseSizeBeforeDelete = bankAccountRepository.findAll().collectList().map((List list)->list.size());

        // Get the bankAccount
        this.client.delete().uri("/api/bank-accounts/{id}", bankAccount.getId())
            .exchange()
            .expectStatus().isOk();

        // Validate the database is empty
        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list)->list.size()),databaseSizeBeforeDelete)
            .flatMap((Tuple2 tuple)->{
                assertTrue(tuple.getT1().equals((Integer)tuple.getT2()+1));
                return null;
            })
            .subscribe();
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BankAccount.class);
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setId("id1");
        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setId(bankAccount1.getId());
        assertThat(bankAccount1).isEqualTo(bankAccount2);
        bankAccount2.setId("id2");
        assertThat(bankAccount1).isNotEqualTo(bankAccount2);
        bankAccount1.setId(null);
        assertThat(bankAccount1).isNotEqualTo(bankAccount2);
    }
}
