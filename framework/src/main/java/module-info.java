import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.RouterManagementMySQLAdapter;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.SwitchManagementMySQLAdapter;
import dev.hugodesouzacaramez.topologyinventory.application.ports.output.RouterManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.application.usecases.NetworkManagementUseCase;
import dev.hugodesouzacaramez.topologyinventory.application.usecases.RouterManagementUseCase;
import dev.hugodesouzacaramez.topologyinventory.application.usecases.SwitchManagementUseCase;

module framework {
    requires domain;
    requires application;
    requires static lombok;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires quarkus.hibernate.reactive.panache;
    requires jakarta.persistence;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires jakarta.ws.rs;
    requires io.smallrye.mutiny;
    requires io.vertx.core;
    requires quarkus.hibernate.reactive.panache.common;
    requires jakarta.xml.bind;
    requires io.smallrye.common.annotation;
    requires com.fasterxml.jackson.annotation;
    requires microprofile.openapi.api;
    requires jakarta.transaction;
    requires quarkus.vertx;
    requires microprofile.context.propagation.api;

    exports dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.data;
    opens dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.data;

    provides RouterManagementOutputPort
            with RouterManagementMySQLAdapter;
    provides SwitchManagementOutputPort
            with SwitchManagementMySQLAdapter;

    uses RouterManagementUseCase;
    uses SwitchManagementUseCase;
    uses NetworkManagementUseCase;
    uses RouterManagementOutputPort;
    uses SwitchManagementOutputPort;
}