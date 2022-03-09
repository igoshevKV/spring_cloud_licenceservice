package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@ToString
@RedisHash("organization")
@Accessors(chain = true)
public class Organization extends RepresentationModel<Organization> {

    @Id
    private String id;
    private String name;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
}
