package com.optimagrowth.license.service;

import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;

    @Transactional
    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Transactional
    public Organization getByName(String name) {
        return organizationRepository.findByName(name);
    }

    @Transactional
    public Organization getById(String organizationId) {
        return organizationRepository.findByOrganizationId(organizationId);
    }

}
