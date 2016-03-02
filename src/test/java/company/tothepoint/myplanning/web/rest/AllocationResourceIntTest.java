package company.tothepoint.myplanning.web.rest;

import company.tothepoint.myplanning.Application;
import company.tothepoint.myplanning.domain.Allocation;
import company.tothepoint.myplanning.repository.AllocationRepository;
import company.tothepoint.myplanning.repository.search.AllocationSearchRepository;

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

import company.tothepoint.myplanning.domain.enumeration.AllocationStatus;

/**
 * Test class for the AllocationResource REST controller.
 *
 * @see AllocationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AllocationResourceIntTest {


    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    
    private static final AllocationStatus DEFAULT_STATUS = AllocationStatus.UNCONFIRMED;
    private static final AllocationStatus UPDATED_STATUS = AllocationStatus.CONFIRMED;

    @Inject
    private AllocationRepository allocationRepository;

    @Inject
    private AllocationSearchRepository allocationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAllocationMockMvc;

    private Allocation allocation;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AllocationResource allocationResource = new AllocationResource();
        ReflectionTestUtils.setField(allocationResource, "allocationSearchRepository", allocationSearchRepository);
        ReflectionTestUtils.setField(allocationResource, "allocationRepository", allocationRepository);
        this.restAllocationMockMvc = MockMvcBuilders.standaloneSetup(allocationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        allocation = new Allocation();
        allocation.setStartDate(DEFAULT_START_DATE);
        allocation.setEndDate(DEFAULT_END_DATE);
        allocation.setStatus(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createAllocation() throws Exception {
        int databaseSizeBeforeCreate = allocationRepository.findAll().size();

        // Create the Allocation

        restAllocationMockMvc.perform(post("/api/allocations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(allocation)))
                .andExpect(status().isCreated());

        // Validate the Allocation in the database
        List<Allocation> allocations = allocationRepository.findAll();
        assertThat(allocations).hasSize(databaseSizeBeforeCreate + 1);
        Allocation testAllocation = allocations.get(allocations.size() - 1);
        assertThat(testAllocation.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testAllocation.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testAllocation.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = allocationRepository.findAll().size();
        // set the field null
        allocation.setStartDate(null);

        // Create the Allocation, which fails.

        restAllocationMockMvc.perform(post("/api/allocations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(allocation)))
                .andExpect(status().isBadRequest());

        List<Allocation> allocations = allocationRepository.findAll();
        assertThat(allocations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = allocationRepository.findAll().size();
        // set the field null
        allocation.setEndDate(null);

        // Create the Allocation, which fails.

        restAllocationMockMvc.perform(post("/api/allocations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(allocation)))
                .andExpect(status().isBadRequest());

        List<Allocation> allocations = allocationRepository.findAll();
        assertThat(allocations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = allocationRepository.findAll().size();
        // set the field null
        allocation.setStatus(null);

        // Create the Allocation, which fails.

        restAllocationMockMvc.perform(post("/api/allocations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(allocation)))
                .andExpect(status().isBadRequest());

        List<Allocation> allocations = allocationRepository.findAll();
        assertThat(allocations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAllocations() throws Exception {
        // Initialize the database
        allocationRepository.saveAndFlush(allocation);

        // Get all the allocations
        restAllocationMockMvc.perform(get("/api/allocations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(allocation.getId().intValue())))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getAllocation() throws Exception {
        // Initialize the database
        allocationRepository.saveAndFlush(allocation);

        // Get the allocation
        restAllocationMockMvc.perform(get("/api/allocations/{id}", allocation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(allocation.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAllocation() throws Exception {
        // Get the allocation
        restAllocationMockMvc.perform(get("/api/allocations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAllocation() throws Exception {
        // Initialize the database
        allocationRepository.saveAndFlush(allocation);

		int databaseSizeBeforeUpdate = allocationRepository.findAll().size();

        // Update the allocation
        allocation.setStartDate(UPDATED_START_DATE);
        allocation.setEndDate(UPDATED_END_DATE);
        allocation.setStatus(UPDATED_STATUS);

        restAllocationMockMvc.perform(put("/api/allocations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(allocation)))
                .andExpect(status().isOk());

        // Validate the Allocation in the database
        List<Allocation> allocations = allocationRepository.findAll();
        assertThat(allocations).hasSize(databaseSizeBeforeUpdate);
        Allocation testAllocation = allocations.get(allocations.size() - 1);
        assertThat(testAllocation.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testAllocation.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testAllocation.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void deleteAllocation() throws Exception {
        // Initialize the database
        allocationRepository.saveAndFlush(allocation);

		int databaseSizeBeforeDelete = allocationRepository.findAll().size();

        // Get the allocation
        restAllocationMockMvc.perform(delete("/api/allocations/{id}", allocation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Allocation> allocations = allocationRepository.findAll();
        assertThat(allocations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
