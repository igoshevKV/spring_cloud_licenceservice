package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LicenseService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Transactional
    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository.findByLicenseIdAndOrganizationId(licenseId, organizationId);
        if (license == null) {
            throw new IllegalArgumentException(String.format(
                    messageSource.getMessage("license.search.error.message",
                            null,
                            null), licenseId, organizationId));
        }
        return license.withComment(config.getProperty());
    }

    @Transactional
    public License getLicense(String organizationId, String licenseId, String clientType) {
        License license = licenseRepository.findByLicenseIdAndOrganizationId(licenseId, organizationId);
        if (license == null) {
            throw new IllegalArgumentException(String.format(
                    messageSource.getMessage("license.search.error.message",
                            null,
                            null), licenseId, organizationId));
        }

        Organization organization = retrieveOrganizationInfo(organizationId, clientType);
        return license.withComment(config.getProperty());
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;
        switch (clientType) {
            case "discovery":
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            case "rest":

                break;
            case "feign":
                organization = organizationFeignClient.getOrganizationByFeign(organizationId);
                break;

        }

        return organization;
    }

    @Transactional
    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        if (organizationService.getById(license.getOrganizationId()) == null) {
            Organization organization = new Organization();
            organization.setOrganizationId(license.getOrganizationId());
            organization.setName(license.getOrganizationId());
            organizationService.createOrganization(organization);
        }
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    @Transactional
    public License updateLicense(License license, String organizationId) {
        license.setOrganizationId(organizationId);
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    @Transactional
    public String deleteLicense(String licenseId) {
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(
                messageSource.getMessage("license.delete.message", null, null), licenseId);

        return responseMessage;
    }

    @Transactional
    public List<License> getAllLicenses() {
        return licenseRepository.getAll();
    }
}
