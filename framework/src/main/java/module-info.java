module framework {
    requires domain;
    requires application;
    requires static lombok;
    requires org.eclipse.persistence.core;
    requires java.sql;
    requires jakarta.persistence;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires io.smallrye.mutiny;
    requires microprofile.openapi.api;
    requires jakarta.ws.rs;

    exports dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.data;
    opens dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.data;

    provides dev.hugodesouzacaramez.topologyinventory.application.ports.output.RouterManagementOutputPort
            with dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.RouterManagementH2Adapter;
    provides dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort
            with dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.SwitchManagementH2Adapter;

    uses dev.hugodesouzacaramez.topologyinventory.application.usecases.RouterManagementUseCase;
    uses dev.hugodesouzacaramez.topologyinventory.application.usecases.SwitchManagementUseCase;
    uses dev.hugodesouzacaramez.topologyinventory.application.usecases.NetworkManagementUseCase;
    uses dev.hugodesouzacaramez.topologyinventory.application.ports.output.RouterManagementOutputPort;
    uses dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort;
}