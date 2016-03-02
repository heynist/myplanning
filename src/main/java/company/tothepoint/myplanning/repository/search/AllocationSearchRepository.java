package company.tothepoint.myplanning.repository.search;

import company.tothepoint.myplanning.domain.Allocation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Allocation entity.
 */
public interface AllocationSearchRepository extends ElasticsearchRepository<Allocation, Long> {
}
