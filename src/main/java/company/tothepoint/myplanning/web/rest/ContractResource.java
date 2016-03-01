package company.tothepoint.myplanning.web.rest;

import com.codahale.metrics.annotation.Timed;
import company.tothepoint.myplanning.domain.Contract;
import company.tothepoint.myplanning.repository.ContractRepository;
import company.tothepoint.myplanning.repository.search.ContractSearchRepository;
import company.tothepoint.myplanning.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Contract.
 */
@RestController
@RequestMapping("/api")
public class ContractResource {

    private final Logger log = LoggerFactory.getLogger(ContractResource.class);
        
    @Inject
    private ContractRepository contractRepository;
    
    @Inject
    private ContractSearchRepository contractSearchRepository;
    
    /**
     * POST  /contracts -> Create a new contract.
     */
    @RequestMapping(value = "/contracts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Contract> createContract(@Valid @RequestBody Contract contract) throws URISyntaxException {
        log.debug("REST request to save Contract : {}", contract);
        if (contract.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contract", "idexists", "A new contract cannot already have an ID")).body(null);
        }
        Contract result = contractRepository.save(contract);
        contractSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/contracts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contract", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contracts -> Updates an existing contract.
     */
    @RequestMapping(value = "/contracts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Contract> updateContract(@Valid @RequestBody Contract contract) throws URISyntaxException {
        log.debug("REST request to update Contract : {}", contract);
        if (contract.getId() == null) {
            return createContract(contract);
        }
        Contract result = contractRepository.save(contract);
        contractSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contract", contract.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contracts -> get all the contracts.
     */
    @RequestMapping(value = "/contracts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Contract> getAllContracts() {
        log.debug("REST request to get all Contracts");
        return contractRepository.findAll();
            }

    /**
     * GET  /contracts/:id -> get the "id" contract.
     */
    @RequestMapping(value = "/contracts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Contract> getContract(@PathVariable Long id) {
        log.debug("REST request to get Contract : {}", id);
        Contract contract = contractRepository.findOne(id);
        return Optional.ofNullable(contract)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contracts/:id -> delete the "id" contract.
     */
    @RequestMapping(value = "/contracts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        log.debug("REST request to delete Contract : {}", id);
        contractRepository.delete(id);
        contractSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contract", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contracts/:query -> search for the contract corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/contracts/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Contract> searchContracts(@PathVariable String query) {
        log.debug("REST request to search Contracts for query {}", query);
        return StreamSupport
            .stream(contractSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
