package dev.hugodesouzacaramez.topologyinventory.domain.entity;

import dev.hugodesouzacaramez.topologyinventory.domain.specification.EmptyRouterSpec;
import dev.hugodesouzacaramez.topologyinventory.domain.specification.EmptySwitchSpec;
import dev.hugodesouzacaramez.topologyinventory.domain.specification.SameCountrySpec;
import dev.hugodesouzacaramez.topologyinventory.domain.specification.SameIpSpec;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.IP;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Location;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Model;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.RouterType;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Vendor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public final class CoreRouter extends Router {

    @Setter
    private Map<Id, Router> routers;

    @Builder
    public CoreRouter(Id id, Vendor vendor, Model model, IP ip, Location location, RouterType routerType, Map<Id, Router> routers) {
        super(id, vendor, model, ip, location, routerType);
        this.routers = routers == null ? new HashMap<Id, Router>() : routers;
    }

    public CoreRouter addRouter(Router anyRouter) {
        var sameCountryRouterSpec = new SameCountrySpec(this);
        var sameIpSpec = new SameIpSpec(this);

        sameCountryRouterSpec.check(anyRouter);
        sameIpSpec.check(anyRouter);

        this.routers.put(anyRouter.id, anyRouter);

        return this;
    }

    public Router removeRouter(Router anyRouter) {
        var emptyRoutersSpec = new EmptyRouterSpec();
        var emptySwitchSpec = new EmptySwitchSpec();

        switch (anyRouter.routerType) {
            case CORE:
                var coreRouter = (CoreRouter)anyRouter;
                emptyRoutersSpec.check(coreRouter);
                break;
            case EDGE:
                var edgeRouter = (EdgeRouter)anyRouter;
                emptySwitchSpec.check(edgeRouter);
        }
        return this.routers.remove(anyRouter.id);
    }
}