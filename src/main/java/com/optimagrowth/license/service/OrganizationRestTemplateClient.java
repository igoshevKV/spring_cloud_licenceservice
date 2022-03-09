package com.optimagrowth.license.service;

import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import com.optimagrowth.license.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrganizationRedisRepository redisRepository;

    @Autowired
    UserContext userContext;

    private Organization checkRedisCache(String organizationId) {
        try {
            return getRedisCachedOrganization(organizationId);
        } catch (Exception ex) {
            logger.debug("Error encountered while trying to retrieve organization {} check Redis Cache, Exception {}",
                    organizationId, ex);
        }
        return null;
    }

    public void cacheOrganizationObject(Organization organization) {
        try {
            redisRepository.save(organization);
        } catch (Exception ex) {
            logger.debug("Unable to cache organization {} in Redis. Excetpion {}",
                    organization, ex);
        }
    }

    public Organization getRedisCachedOrganization(String organizationId){
        return redisRepository.findById(organizationId).orElse(null);
    }

    public void deleteOrganizationFromRedisCache(String organizationId){
        redisRepository.deleteById(organizationId);
    }

    public Organization setAndGetOrganization(String organizationId) {
        logger.debug("In Licensing Service.getOrganization: {}",
                userContext.getCorrelationId());

        Organization organization = checkRedisCache(organizationId);
        if (organization != null) {
            logger.debug("I have successfully retrieved an organization {} from Redis Cache: {}",
                    organizationId, organization);
            return organization;
        }

        logger.debug("Unable to locate organization from Redis Cache: {}", organizationId);

        ResponseEntity<Organization> restExchange = restTemplate.exchange(
                "http://localhost:8072/organization/v1/organization/{organizationId}",
                HttpMethod.GET,
                null,
                Organization.class,
                organizationId);

        organization = restExchange.getBody();
        if (organization != null) {
            cacheOrganizationObject(organization);
        }
        return restExchange.getBody();
    }

}
