package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;


@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "licenses")
public class License extends RepresentationModel<License> {
    @Id
    @Column(name = "license_id", nullable = false)
    private String licenseId;
    private String description;

    @Column(name = "organization_id", nullable = false)
//    @ManyToOne
//    @JoinColumn(name = "organization_id")
    private String organizationId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "license_type", nullable = false)
    private String licenseType;

    private String comment;

    public License withComment(String comment) {
        this.setComment(comment);
        return this;
    }
}
