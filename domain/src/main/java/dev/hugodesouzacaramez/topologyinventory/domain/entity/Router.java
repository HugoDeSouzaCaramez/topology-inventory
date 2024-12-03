package dev.hugodesouzacaramez.topologyinventory.domain.entity;

import dev.hugodesouzacaramez.topologyinventory.domain.vo.IP;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Location;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Model;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.RouterType;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Vendor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

@Getter
public abstract sealed class Router extends Equipment permits CoreRouter, EdgeRouter {

    protected final RouterType routerType;

    @Setter
    public Id parentRouterId;

    public static Predicate<Equipment> getRouterTypePredicate(RouterType routerType){
        return r -> ((Router)r).getRouterType().equals(routerType);
    }

    public static Predicate<Equipment> getModelPredicate(Model model){
        return r -> r.getModel().equals(model);
    }

    public static Predicate<Equipment> getCountryPredicate(Location location){
        return p -> p.location.country().equals(location.country());
    }

    public Router(Id id, Id parentRouterId, Vendor vendor, Model model, IP ip, Location location, RouterType routerType) {
        super(id, vendor, model, ip, location);
        this.routerType = routerType;
        this.parentRouterId = parentRouterId;
    }
}