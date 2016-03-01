package company.tothepoint.myplanning.repository.search;

import company.tothepoint.myplanning.domain.Contract;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Contract entity.
 */
public interface ContractSearchRepository extends ElasticsearchRepository<Contract, Long> {
}
