package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Integer> {

    Organization findByName(@Param("name") String name);

    Organization findByOrganizationId(@Param("organizationId") String organizationId);

}
