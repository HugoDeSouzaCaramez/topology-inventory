package dev.hugodesouzacaramez.topologyinventory.application.ports.output;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.Router;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;

public interface RouterManagementOutputPort {
    Router retrieveRouter(Id id);

    boolean removeRouter(Id id);

    Router persistRouter(Router router);
}
