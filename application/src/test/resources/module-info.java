module application {
    requires domain;
    requires static lombok;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires arc;

    exports dev.hugodesouzacaramez.topologyinventory.application.ports.input;
    exports dev.hugodesouzacaramez.topologyinventory.application.ports.output;
    exports dev.hugodesouzacaramez.topologyinventory.application.usecases;

    provides dev.hugodesouzacaramez.topologyinventory.application.usecases.RouterManagementUseCase
            with dev.hugodesouzacaramez.topologyinventory.application.ports.input.RouterManagementInputPort;
    provides dev.hugodesouzacaramez.topologyinventory.application.usecases.SwitchManagementUseCase
            with dev.hugodesouzacaramez.topologyinventory.application.ports.input.SwitchManagementInputPort;
    provides dev.hugodesouzacaramez.topologyinventory.application.usecases.NetworkManagementUseCase
            with dev.hugodesouzacaramez.topologyinventory.application.ports.input.NetworkManagementInputPort;
}