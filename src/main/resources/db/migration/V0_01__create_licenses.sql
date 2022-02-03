DO
$$
    BEGIN
        CREATE TABLE if not exists licenses
        (
            licenseId      varchar NOT NULL,
            description    varchar NOT NULL,
            organizationId varchar NOT NULL,
            productName    varchar NOT NULL,
            licenseType    varchar NOT NULL
        );
    END;
$$;