package dev.hugodesouzacaramez.topologyinventory.application.ports.input;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.RouterManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.application.usecases.RouterManagementUseCase;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.CoreRouter;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Router;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.factory.RouterFactory;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.IP;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Location;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Model;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.RouterType;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Vendor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RouterManagementInputPort implements RouterManagementUseCase {

    @Inject
    RouterManagementOutputPort routerManagementOutputPort;

    @Override
    public Router createRouter(Id id,
                               Vendor vendor,
                               Model model,
                               IP ip,
                               Location location,
                               RouterType routerType) {
        return RouterFactory.getRouter(null,
                vendor,
                model,
                ip,
                location,
                routerType
        );
    }

    @Override
    public boolean removeRouter(Id id) {
        return routerManagementOutputPort.removeRouter(id);
    }

    @Override
    public Router retrieveRouter(Id id) {
        return routerManagementOutputPort.retrieveRouter(id);
    }

    @Override
    public Router persistRouter(Router router) {
        return routerManagementOutputPort.persistRouter(router);
    }

    @Override
    public CoreRouter addRouterToCoreRouter(Router router, CoreRouter coreRouter) {
        var addedRouter = coreRouter.addRouter(router);
        return (CoreRouter) persistRouter(addedRouter);
    }

    @Override
    public Router removeRouterFromCoreRouter(Router router, CoreRouter coreRouter) {
        var removedRouter = coreRouter.removeRouter(router);
        return persistRouter(removedRouter);
    }

    @Override
    public Router changeLocation(Router router, Location location) {
        router.changeLocation(location);
        return persistRouter(router);
    }
}