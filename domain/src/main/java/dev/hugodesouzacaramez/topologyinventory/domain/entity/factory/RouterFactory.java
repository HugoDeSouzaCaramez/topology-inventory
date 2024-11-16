package dev.hugodesouzacaramez.topologyinventory.domain.entity.factory;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.CoreRouter;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.EdgeRouter;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Router;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.IP;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Location;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Model;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.RouterType;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Vendor;

public class RouterFactory {

    public static Router getRouter(Id id,
                                   Vendor vendor,
                                   Model model,
                                   IP ip,
                                   Location location,
                                   RouterType routerType){

        switch (routerType) {
            case CORE -> {
                return CoreRouter.builder().
                        id(id == null ? Id.withoutId() : id).
                        vendor(vendor).
                        model(model).
                        ip(ip).
                        location(location).
                        routerType(routerType).
                        build();
            }
            case EDGE -> {
                return EdgeRouter.builder().
                        id(id==null ? Id.withoutId():id).
                        vendor(vendor).
                        model(model).
                        ip(ip).
                        location(location).
                        routerType(routerType).
                        build();
            }
            default -> throw new UnsupportedOperationException( "No valid router type informed");
        }
    }
}
