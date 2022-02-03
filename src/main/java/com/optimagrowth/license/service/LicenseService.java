package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LicenseService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(licenseId, organizationId);
        if (license == null) {
            throw new IllegalArgumentException(String.format(
                    messageSource.getMessage("license.search.error.message",
                            null,
                            null), licenseId, organizationId));
        }
        return license.withComment(config.getProperty());
    }

    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license, String organizationId) {
        license.setOrganizationId(organizationId);
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId) {
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(
                messageSource.getMessage("license.delete.message", null, null), licenseId);

        return responseMessage;
    }

}
