module bootstrap {
    requires domain;
    requires framework;
    requires application;
    requires jakarta.persistence;
    requires transitive quarkus.core;
    requires jakarta.validation;
    requires jakarta.ws.rs;
    requires jakarta.transaction;
    requires static lombok;
}