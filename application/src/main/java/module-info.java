module application {
    exports dev.hugodesouzacaramez.topologyinventory.application.ports.input;
    exports dev.hugodesouzacaramez.topologyinventory.application.usecases;
    exports dev.hugodesouzacaramez.topologyinventory.application.ports.output;
    requires domain;
    requires static lombok;
    requires com.fasterxml.jackson.annotation;
}