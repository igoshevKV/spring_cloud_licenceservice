package com.optimagrowth.license.service;

import com.optimagrowth.license.model.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "organization-service")
public interface OrganizationFeignClient {

    @RequestMapping(method = RequestMethod.GET,
            value = "/v1/organization/{organizationId}",
            consumes = "application/json")
    Organization getOrganizationByFeign(@PathVariable("organizationId") String organizationId);
}
