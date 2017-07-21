//package jhipster.reactive.web.rest;
//
//import jhipster.reactive.DefaultMongoDbApp;
//
//import jhipster.reactive.domain.Operation;
//import jhipster.reactive.repository.OperationRepository;
//import jhipster.reactive.web.rest.errors.ExceptionTranslator;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.junit.Assert.assertTrue;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Test class for the OperationResource REST controller.
// *
// * @see OperationResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = DefaultMongoDbApp.class)
//public class OperationResourceIntTest {
//
//    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
//    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
//
//    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
//    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";
//
//    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
//    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
//
//    @Autowired
//    private OperationRepository operationRepository;
//
//    @Autowired
//    private MappingJackson2HttpMessageConverter jacksonMessageConverter;
//
//    @Autowired
//    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
//
//    @Autowired
//    private ExceptionTranslator exceptionTranslator;
//
//    private MockMvc restOperationMockMvc;
//
//    private Operation operation;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        OperationResource operationResource = new OperationResource(operationRepository);
//        this.restOperationMockMvc = MockMvcBuilders.standaloneSetup(operationResource)
//            .setCustomArgumentResolvers(pageableArgumentResolver)
//            .setControllerAdvice(exceptionTranslator)
//            .setMessageConverters(jacksonMessageConverter).build();
//    }
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Operation createEntity() {
//        Operation operation = new Operation();
//        operation.setDate(DEFAULT_DATE);
//        operation.setDescription(DEFAULT_DESCRIPTION);
//        operation.setAmount(DEFAULT_AMOUNT);
//        return operation;
//    }
//
//    @Before
//    public void initTest() {
//        operationRepository.deleteAll();
//        operation = createEntity();
//    }
//
//    @Test
//    public void createOperation() throws Exception {
//        int databaseSizeBeforeCreate = operationRepository.findAll().size();
//
//        // Create the Operation
//        restOperationMockMvc.perform(post("/api/operations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(operation)))
//            .andExpect(status().isCreated());
//
//        // Validate the Operation in the database
//        List<Operation> operationList = operationRepository.findAll();
//        assertThat(operationList).hasSize(databaseSizeBeforeCreate + 1);
//        Operation testOperation = operationList.get(operationList.size() - 1);
//        assertThat(testOperation.getDate()).isEqualTo(DEFAULT_DATE);
//        assertThat(testOperation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
//        assertThat(testOperation.getAmount()).isEqualTo(DEFAULT_AMOUNT);
//    }
//
//    @Test
//    public void createOperationWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = operationRepository.findAll().size();
//
//        // Create the Operation with an existing ID
//        operation.setId("existing_id");
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restOperationMockMvc.perform(post("/api/operations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(operation)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<Operation> operationList = operationRepository.findAll();
//        assertThat(operationList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    public void checkDateIsRequired() throws Exception {
//        int databaseSizeBeforeTest = operationRepository.findAll().size();
//        // set the field null
//        operation.setDate(null);
//
//        // Create the Operation, which fails.
//
//        restOperationMockMvc.perform(post("/api/operations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(operation)))
//            .andExpect(status().isBadRequest());
//
//        List<Operation> operationList = operationRepository.findAll();
//        assertThat(operationList).hasSize(databaseSizeBeforeTest);
//    }
//
//    @Test
//    public void checkAmountIsRequired() throws Exception {
//        int databaseSizeBeforeTest = operationRepository.findAll().size();
//        // set the field null
//        operation.setAmount(null);
//
//        // Create the Operation, which fails.
//
//        restOperationMockMvc.perform(post("/api/operations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(operation)))
//            .andExpect(status().isBadRequest());
//
//        List<Operation> operationList = operationRepository.findAll();
//        assertThat(operationList).hasSize(databaseSizeBeforeTest);
//    }
//
//    @Test
//    public void getAllOperations() throws Exception {
//        // Initialize the database
//        operationRepository.save(operation);
//
//        // Get all the operationList
//        restOperationMockMvc.perform(get("/api/operations?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(operation.getId())))
//            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
//            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
//            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())));
//    }
//
//    @Test
//    public void getOperation() throws Exception {
//        // Initialize the database
//        operationRepository.save(operation);
//
//        // Get the operation
//        restOperationMockMvc.perform(get("/api/operations/{id}", operation.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(operation.getId()))
//            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
//            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
//            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()));
//    }
//
//    @Test
//    public void getNonExistingOperation() throws Exception {
//        // Get the operation
//        restOperationMockMvc.perform(get("/api/operations/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void updateOperation() throws Exception {
//        // Initialize the database
//        operationRepository.save(operation);
//        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
//
//        // Update the operation
//        Optional<Operation> updatedOperation = operationRepository.findById(operation.getId());
//        assertTrue(updatedOperation.isPresent());
//        updatedOperation.get().setDate(UPDATED_DATE);
//        updatedOperation.get().setDescription(UPDATED_DESCRIPTION);
//        updatedOperation.get().setAmount(UPDATED_AMOUNT);
//
//        restOperationMockMvc.perform(put("/api/operations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedOperation.get())))
//            .andExpect(status().isOk());
//
//        // Validate the Operation in the database
//        List<Operation> operationList = operationRepository.findAll();
//        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
//        Operation testOperation = operationList.get(operationList.size() - 1);
//        assertThat(testOperation.getDate()).isEqualTo(UPDATED_DATE);
//        assertThat(testOperation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
//        assertThat(testOperation.getAmount()).isEqualTo(UPDATED_AMOUNT);
//    }
//
//    @Test
//    public void updateNonExistingOperation() throws Exception {
//        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
//
//        // Create the Operation
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restOperationMockMvc.perform(put("/api/operations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(operation)))
//            .andExpect(status().isCreated());
//
//        // Validate the Operation in the database
//        List<Operation> operationList = operationRepository.findAll();
//        assertThat(operationList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    public void deleteOperation() throws Exception {
//        // Initialize the database
//        operationRepository.save(operation);
//        int databaseSizeBeforeDelete = operationRepository.findAll().size();
//
//        // Get the operation
//        restOperationMockMvc.perform(delete("/api/operations/{id}", operation.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate the database is empty
//        List<Operation> operationList = operationRepository.findAll();
//        assertThat(operationList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    public void equalsVerifier() throws Exception {
//        TestUtil.equalsVerifier(Operation.class);
//        Operation operation1 = new Operation();
//        operation1.setId("id1");
//        Operation operation2 = new Operation();
//        operation2.setId(operation1.getId());
//        assertThat(operation1).isEqualTo(operation2);
//        operation2.setId("id2");
//        assertThat(operation1).isNotEqualTo(operation2);
//        operation1.setId(null);
//        assertThat(operation1).isNotEqualTo(operation2);
//    }
//}
