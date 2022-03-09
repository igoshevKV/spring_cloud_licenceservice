package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.service.LicenseService;
import com.optimagrowth.license.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
public class LicenseCustomController {

    public static final Logger logger = LoggerFactory.getLogger(LicenseCustomController.class);

    @Autowired
    LicenseService licenseService;

    @PostMapping(value = "/createLicense")
    public ResponseEntity<License> createLicense(@RequestBody License request) {
        return ResponseEntity.ok(licenseService.createLicense(request));
    }

    @GetMapping(value = "/getAllLicenses")
    public List<License> getAll() throws TimeoutException {
        logger.debug("LicenseCustomController Correlation id:{}", UserContextHolder.getContext().getCorrelationId());
        return licenseService.getAllLicenses();
    }

    @GetMapping(value = "/getOrganizationFromOrgserv")
    public Organization getOrganizationFromOrgserv(
            @RequestParam("organizationId") String organizationId,
            @RequestParam("clientType") String clientType) throws TimeoutException {

        logger.debug("LicenseCustomController Correlation id:{}", UserContextHolder.getContext().getCorrelationId());
        return licenseService.getOrganizationFromOrgserv(organizationId, clientType);
    }
}
