package com.optimagrowth.license.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {

    @Input("inboundOrgChanges")
    SubscribableChannel orgs();

    @Output("outboundOrg")
    MessageChannel outboundOrg();
}
