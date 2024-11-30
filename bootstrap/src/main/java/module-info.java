module bootstrap {
    requires domain;
    requires framework;
    requires application;
    requires jakarta.persistence;
    requires transitive quarkus.core;
}