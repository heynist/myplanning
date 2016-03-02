package company.tothepoint.myplanning.web.rest;

import company.tothepoint.myplanning.Application;
import company.tothepoint.myplanning.domain.Contract;
import company.tothepoint.myplanning.repository.ContractRepository;
import company.tothepoint.myplanning.repository.search.ContractSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ContractResource REST controller.
 *
 * @see ContractResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ContractResourceIntTest {


    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_CONTRACT_NUMBER = "AAAAA";
    private static final String UPDATED_CONTRACT_NUMBER = "BBBBB";

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final Integer DEFAULT_MONTH = 1;
    private static final Integer UPDATED_MONTH = 2;

    @Inject
    private ContractRepository contractRepository;

    @Inject
    private ContractSearchRepository contractSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restContractMockMvc;

    private Contract contract;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContractResource contractResource = new ContractResource();
        ReflectionTestUtils.setField(contractResource, "contractSearchRepository", contractSearchRepository);
        ReflectionTestUtils.setField(contractResource, "contractRepository", contractRepository);
        this.restContractMockMvc = MockMvcBuilders.standaloneSetup(contractResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        contract = new Contract();
        contract.setStartDate(DEFAULT_START_DATE);
        contract.setEndDate(DEFAULT_END_DATE);
        contract.setContractNumber(DEFAULT_CONTRACT_NUMBER);
        contract.setYear(DEFAULT_YEAR);
        contract.setMonth(DEFAULT_MONTH);
    }

    @Test
    @Transactional
    public void createContract() throws Exception {
        int databaseSizeBeforeCreate = contractRepository.findAll().size();

        // Create the Contract

        restContractMockMvc.perform(post("/api/contracts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contract)))
                .andExpect(status().isCreated());

        // Validate the Contract in the database
        List<Contract> contracts = contractRepository.findAll();
        assertThat(contracts).hasSize(databaseSizeBeforeCreate + 1);
        Contract testContract = contracts.get(contracts.size() - 1);
        assertThat(testContract.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testContract.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testContract.getContractNumber()).isEqualTo(DEFAULT_CONTRACT_NUMBER);
        assertThat(testContract.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testContract.getMonth()).isEqualTo(DEFAULT_MONTH);
    }

    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = contractRepository.findAll().size();
        // set the field null
        contract.setStartDate(null);

        // Create the Contract, which fails.

        restContractMockMvc.perform(post("/api/contracts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contract)))
                .andExpect(status().isBadRequest());

        List<Contract> contracts = contractRepository.findAll();
        assertThat(contracts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllContracts() throws Exception {
        // Initialize the database
        contractRepository.saveAndFlush(contract);

        // Get all the contracts
        restContractMockMvc.perform(get("/api/contracts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(contract.getId().intValue())))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
                .andExpect(jsonPath("$.[*].contractNumber").value(hasItem(DEFAULT_CONTRACT_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
                .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH)));
    }

    @Test
    @Transactional
    public void getContract() throws Exception {
        // Initialize the database
        contractRepository.saveAndFlush(contract);

        // Get the contract
        restContractMockMvc.perform(get("/api/contracts/{id}", contract.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(contract.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.contractNumber").value(DEFAULT_CONTRACT_NUMBER.toString()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.month").value(DEFAULT_MONTH));
    }

    @Test
    @Transactional
    public void getNonExistingContract() throws Exception {
        // Get the contract
        restContractMockMvc.perform(get("/api/contracts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContract() throws Exception {
        // Initialize the database
        contractRepository.saveAndFlush(contract);

		int databaseSizeBeforeUpdate = contractRepository.findAll().size();

        // Update the contract
        contract.setStartDate(UPDATED_START_DATE);
        contract.setEndDate(UPDATED_END_DATE);
        contract.setContractNumber(UPDATED_CONTRACT_NUMBER);
        contract.setYear(UPDATED_YEAR);
        contract.setMonth(UPDATED_MONTH);

        restContractMockMvc.perform(put("/api/contracts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contract)))
                .andExpect(status().isOk());

        // Validate the Contract in the database
        List<Contract> contracts = contractRepository.findAll();
        assertThat(contracts).hasSize(databaseSizeBeforeUpdate);
        Contract testContract = contracts.get(contracts.size() - 1);
        assertThat(testContract.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testContract.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testContract.getContractNumber()).isEqualTo(UPDATED_CONTRACT_NUMBER);
        assertThat(testContract.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testContract.getMonth()).isEqualTo(UPDATED_MONTH);
    }

    @Test
    @Transactional
    public void deleteContract() throws Exception {
        // Initialize the database
        contractRepository.saveAndFlush(contract);

		int databaseSizeBeforeDelete = contractRepository.findAll().size();

        // Get the contract
        restContractMockMvc.perform(delete("/api/contracts/{id}", contract.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Contract> contracts = contractRepository.findAll();
        assertThat(contracts).hasSize(databaseSizeBeforeDelete - 1);
    }
}
