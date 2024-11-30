package dev.hugodesouzacaramez.topologyinventory.application.mocks;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.RouterManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Router;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import io.quarkus.test.Mock;

@Mock
public class RouterManagementOutputPortMock implements RouterManagementOutputPort {
    @Override
    public Router retrieveRouter(Id id) {
        return null;
    }

    @Override
    public Router removeRouter(Id id) {
        return null;
    }

    @Override
    public Router persistRouter(Router router) {
        return null;
    }
}