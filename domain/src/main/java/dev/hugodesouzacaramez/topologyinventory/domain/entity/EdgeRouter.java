package dev.hugodesouzacaramez.topologyinventory.domain.entity;

import dev.hugodesouzacaramez.topologyinventory.domain.specification.EmptyNetworkSpec;
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

import java.util.Map;

@Getter
@ToString
public final class EdgeRouter extends Router {

    @Setter
    private Map<Id, Switch> switches;

    @Builder
    public EdgeRouter(Id id, Id parentRouterId, Vendor vendor, Model model, IP ip, Location location, RouterType routerType, Map<Id, Switch> switches) {
        super(id, parentRouterId, vendor, model, ip, location, routerType);
        this.switches = switches;
    }

    public void addSwitch(Switch anySwitch) {
        var sameCountryRouterSpec = new SameCountrySpec(this);
        var sameIpSpec = new SameIpSpec(this);

        sameCountryRouterSpec.check(anySwitch);
        sameIpSpec.check(anySwitch);

        this.switches.put(anySwitch.id,anySwitch);
    }

    public Switch removeSwitch(Switch anySwitch) {
        var emptyNetworkSpec = new EmptyNetworkSpec();
        emptyNetworkSpec.check(anySwitch);

        return this.switches.remove(anySwitch.id);
    }
}