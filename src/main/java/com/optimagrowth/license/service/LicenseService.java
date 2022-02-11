package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.aspectj.weaver.ast.Or;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

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

    @Autowired
    OrganizationRestTemplateClient organizationRestTemplateClient;

    @CircuitBreaker(name = "licenseService")
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


        return license.withComment(config.getProperty());
    }
    /* Throwable t - it's required parameter for FALLBACK method */
    private Organization buildFallbackOrganization(String organizationId, String clientType, Throwable t){
        return new Organization()
                .setOrganizationId("0000-0000")
                .setName("just created Organization and not commited")
                .setContactName("no one")
                .setContactEmail("it's doesn't exist");
    }

    @CircuitBreaker(name = "organizationService", fallbackMethod = "buildFallbackOrganization")
    public Organization getOrganizationFromOrgserv(String organizationId, String clientType) throws TimeoutException{
        Organization organization = retrieveOrganizationInfo(organizationId, clientType);
        return organization;
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) throws TimeoutException{
        randomlyRun(); /* imitation duration */
        Organization organization = null;
        switch (clientType) {
            case "discovery":
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            case "rest":
                organization = organizationRestTemplateClient.getOrganization(organizationId);
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
    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackGetDefaultLicenseAtList")
    @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackGetDefaultLicenseAtList")
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackGetDefaultLicenseAtList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackGetDefaultLicenseAtList")
    public List<License> getAllLicenses() throws TimeoutException{
        randomlyRun(); /* time duration */
        return licenseRepository.getAll();
    }

    private List<License> buildFallbackGetDefaultLicenseAtList(Throwable t){
        List<License> list = new ArrayList<>();
        list.add(new License().setLicenseId("0000-0000")
                .setLicenseType("default type")
                .setOrganizationId("default organization")
                .setProductName("default name")
                .setDescription(
                        String.format("default license not commited. Throwable: %s", t)));
        return list;
    }

    private void sleep() throws TimeoutException{
        try {
            int duration = 5000;
            Thread.sleep(duration);
            System.out.println("DURATION: " + duration);
            throw new java.util.concurrent.TimeoutException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void randomlyRun() throws TimeoutException{
        Random random = new Random();
        int r = random.nextInt(3)+1;
        if(r == 3) sleep();
    }
}
