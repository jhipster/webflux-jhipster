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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.constraints.AssertTrue;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

    private MockMvc restBankAccountMockMvc;

    private BankAccount bankAccount;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BankAccountResource bankAccountResource = new BankAccountResource(bankAccountRepository);
        this.restBankAccountMockMvc = MockMvcBuilders.standaloneSetup(bankAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
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
        databaseSizeBeforeCreate.doOnTerminate(( bankAccount1, throwable) -> {
                assertNull(throwable);

                // Create the BankAccount
            try{
                restBankAccountMockMvc.perform(post("/api/bank-accounts")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
                    .andExpect(status().isCreated());
            } catch (Exception e){
                throw new RuntimeException();
            }
            });
        // Validate the BankAccount in the database
        Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
        Mono.when(bankAccountList.collectList().map((List list)->list.size()),databaseSizeBeforeCreate)
            .map((Tuple2 tuple)->{
                assertTrue(tuple.getT1().equals((Integer)tuple.getT2()+1));
                return null;
            }).subscribe();

        Mono<BankAccount> testBankAccount = bankAccountList.last();
        testBankAccount.map((BankAccount lastBankAccount)->{
            assertThat(lastBankAccount.getName()).isEqualTo(UPDATED_NAME);
            assertThat(lastBankAccount.getBalance()).isEqualTo(UPDATED_BALANCE);
            return null;
        }).subscribe();

    }

    @Test
    public void createBankAccountWithExistingId() throws Exception {
        Mono<Integer> databaseSizeBeforeCreate = bankAccountRepository.findAll().collectList().map((List list)->list.size());

        // Create the BankAccount with an existing ID
        bankAccount.setId("existing_id");

        databaseSizeBeforeCreate.doOnTerminate(( bankAccount1, throwable) -> {
            assertNull(throwable);
            try{
            // An entity with an existing ID cannot be created, so this API call must fail
            restBankAccountMockMvc.perform(post("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
                .andExpect(status().isBadRequest());
            } catch (Exception e){
                throw new RuntimeException();
            }
        });
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

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
            .andExpect(status().isBadRequest());

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

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
            .andExpect(status().isBadRequest());

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
        bankAccountRepository.save(bankAccount).doOnTerminate((BankAccount bankAccount1, Throwable throwable) -> {
            assertNull(throwable);

            // Get all the bankAccountList
            try {
                restBankAccountMockMvc.perform(get("/api/bank-accounts?sort=id,desc"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.getId())))
                    .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                    .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE.intValue())));
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void getBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.save(bankAccount).doOnTerminate((BankAccount bankAccount1, Throwable throwable) -> {
            assertNull(throwable);

            // Get the bankAccount
            try{
            restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", bankAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(bankAccount.getId()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE.intValue()));
            } catch (Exception e){
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void getNonExistingBankAccount() throws Exception {
        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.save(bankAccount).doOnTerminate((BankAccount bankAccount1,Throwable throwable)->{
            assertNull(throwable);
            Mono<Integer> databaseSizeBeforeUpdate = bankAccountRepository.findAll().collectList().map((List list)->list.size());

            // Update the bankAccount
            Mono<BankAccount> updatedBankAccount = bankAccountRepository.findById(bankAccount.getId());
            updatedBankAccount
                .hasElement()
                .map((Boolean bool) -> {
                assertTrue(bool);
                return null;
            }).subscribe();
            updatedBankAccount
                .map((BankAccount savedBankAccount)->{
                    savedBankAccount.setName(UPDATED_NAME);
                    savedBankAccount.setBalance(UPDATED_BALANCE);
                    return savedBankAccount;
                })
                .subscribe();

            updatedBankAccount.map((BankAccount savedBankAccount)->{
                try{
                restBankAccountMockMvc.perform(put("/api/bank-accounts")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(savedBankAccount)))
                    .andExpect(status().isOk());
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
        });
    }

    @Test
    public void updateNonExistingBankAccount() throws Exception {
        Mono<Integer> databaseSizeBeforeUpdate = bankAccountRepository.findAll().collectList().map((List list)->list.size());
        databaseSizeBeforeUpdate.doOnTerminate((bankAccount1,throwable)-> {
            // Create the BankAccount
            assertNull(throwable);
            // If the entity doesn't have an ID, it will be created instead of just being updated
            try{
                restBankAccountMockMvc.perform(put("/api/bank-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
                .andExpect(status().isCreated());
             } catch (Exception e){
                throw new RuntimeException();
             }
            // Validate the BankAccount in the database
            Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
            Mono.when(bankAccountList.collectList().map((List list) -> list.size()), databaseSizeBeforeUpdate)
                .map((Tuple2 tuple) -> {
                    assertTrue(tuple.getT1().equals((Integer) tuple.getT2() + 1));
                    return null;
                })
                .subscribe();
        });
    }

    @Test
    public void deleteBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.save(bankAccount).doOnTerminate((BankAccount bankAccount1,Throwable throwable)-> {
            assertNull(throwable);
            Mono<Integer> databaseSizeBeforeDelete = bankAccountRepository.findAll().collectList().map((List list) -> list.size());

            // Get the bankAccount
            try{
            restBankAccountMockMvc.perform(delete("/api/bank-accounts/{id}", bankAccount.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
            } catch (Exception e){
                throw new RuntimeException();
            }

            // Validate the database is empty
            Flux<BankAccount> bankAccountList = bankAccountRepository.findAll();
            Mono.when(bankAccountList.collectList().map((List list) -> list.size()), databaseSizeBeforeDelete)
                .map((Tuple2 tuple) -> {
                    assertTrue(tuple.getT1().equals((Integer) tuple.getT2() + 1));
                    return null;
                })
                .subscribe();
        });
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
