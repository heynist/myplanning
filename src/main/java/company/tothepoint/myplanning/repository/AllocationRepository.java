package company.tothepoint.myplanning.repository;

import company.tothepoint.myplanning.domain.Allocation;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Allocation entity.
 */
public interface AllocationRepository extends JpaRepository<Allocation,Long> {

}
