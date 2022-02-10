package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class LicenseCustomController {

    @Autowired
    LicenseService licenseService;

    @PostMapping(value = "/createLicense")
    public ResponseEntity<License> createLicense(@RequestBody License request) {
        return ResponseEntity.ok(licenseService.createLicense(request));
    }

    @GetMapping(value = "/getAllLicenses")
    public List<License> getAll() throws TimeoutException{
        return licenseService.getAllLicenses();
    }

    @GetMapping(value = "/getOrganizationFromOrgserv")
    public Organization getOrganizationFromOrgserv(
            @RequestParam("organizationId") String organizationId,
            @RequestParam("clientType") String clientType) throws TimeoutException {
        return licenseService.getOrganizationFromOrgserv(organizationId, clientType);
    }
}
