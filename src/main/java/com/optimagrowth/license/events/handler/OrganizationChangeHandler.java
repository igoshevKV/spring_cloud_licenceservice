package com.optimagrowth.license.events.handler;

import com.optimagrowth.license.events.CustomChannels;
import com.optimagrowth.license.events.model.OrganizationChangeModel;
import com.optimagrowth.license.service.OrganizationRestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    @Autowired
    OrganizationRestTemplateClient organizationRestTemplateClient;

    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel organizationChangeModel) {
        logger.debug("Received message from kafka");

        switch (organizationChangeModel.getAction()) {
            case "GET":
            case "CREATED":
            case "UPDATED":
                organizationRestTemplateClient.setAndGetOrganization(organizationChangeModel.getOrganizationId());
                logger.debug("receive {}", organizationChangeModel.getAction());
                break;
            case "DELETED":
                organizationRestTemplateClient.deleteOrganizationFromRedisCache(organizationChangeModel.getOrganizationId());
                logger.debug("receive DELETED");
                break;
            default:
                logger.debug("receive UNKNOWN");
                break;
        }

    }
}

