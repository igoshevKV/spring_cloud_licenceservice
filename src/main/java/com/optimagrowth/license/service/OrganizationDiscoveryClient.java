package com.optimagrowth.license.service;

import com.optimagrowth.license.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/* Получаем список экземпляров сервиса org, и отправляем первому экземпляру запрос */
@Component
public class OrganizationDiscoveryClient {

    @Autowired
    private DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");
        if (instances.size() == 0) {
            return null;
        }

        String serviceUrl = String.format("%s/v1/organization/%s", instances.get(0).getUri().toString(), organizationId);
        ResponseEntity<Organization> restExchange = restTemplate.exchange(serviceUrl,
                HttpMethod.GET,
                null,
                Organization.class, organizationId);
        return restExchange.getBody();
    }
}
