package company.tothepoint.myplanning.web.rest;

import com.codahale.metrics.annotation.Timed;
import company.tothepoint.myplanning.domain.Allocation;
import company.tothepoint.myplanning.repository.AllocationRepository;
import company.tothepoint.myplanning.repository.search.AllocationSearchRepository;
import company.tothepoint.myplanning.web.rest.util.HeaderUtil;
import company.tothepoint.myplanning.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Allocation.
 */
@RestController
@RequestMapping("/api")
public class AllocationResource {

    private final Logger log = LoggerFactory.getLogger(AllocationResource.class);
        
    @Inject
    private AllocationRepository allocationRepository;
    
    @Inject
    private AllocationSearchRepository allocationSearchRepository;
    
    /**
     * POST  /allocations -> Create a new allocation.
     */
    @RequestMapping(value = "/allocations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Allocation> createAllocation(@Valid @RequestBody Allocation allocation) throws URISyntaxException {
        log.debug("REST request to save Allocation : {}", allocation);
        if (allocation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("allocation", "idexists", "A new allocation cannot already have an ID")).body(null);
        }
        Allocation result = allocationRepository.save(allocation);
        allocationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/allocations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("allocation", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /allocations -> Updates an existing allocation.
     */
    @RequestMapping(value = "/allocations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Allocation> updateAllocation(@Valid @RequestBody Allocation allocation) throws URISyntaxException {
        log.debug("REST request to update Allocation : {}", allocation);
        if (allocation.getId() == null) {
            return createAllocation(allocation);
        }
        Allocation result = allocationRepository.save(allocation);
        allocationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("allocation", allocation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /allocations -> get all the allocations.
     */
    @RequestMapping(value = "/allocations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Allocation>> getAllAllocations(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Allocations");
        Page<Allocation> page = allocationRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/allocations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /allocations/:id -> get the "id" allocation.
     */
    @RequestMapping(value = "/allocations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Allocation> getAllocation(@PathVariable Long id) {
        log.debug("REST request to get Allocation : {}", id);
        Allocation allocation = allocationRepository.findOne(id);
        return Optional.ofNullable(allocation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /allocations/:id -> delete the "id" allocation.
     */
    @RequestMapping(value = "/allocations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAllocation(@PathVariable Long id) {
        log.debug("REST request to delete Allocation : {}", id);
        allocationRepository.delete(id);
        allocationSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("allocation", id.toString())).build();
    }

    /**
     * SEARCH  /_search/allocations/:query -> search for the allocation corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/allocations/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Allocation> searchAllocations(@PathVariable String query) {
        log.debug("REST request to search Allocations for query {}", query);
        return StreamSupport
            .stream(allocationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
