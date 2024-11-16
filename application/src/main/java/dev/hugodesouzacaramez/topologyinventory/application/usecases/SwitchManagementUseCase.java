package dev.hugodesouzacaramez.topologyinventory.application.usecases;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.EdgeRouter;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.IP;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Location;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Model;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.SwitchType;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Vendor;

public interface SwitchManagementUseCase {

    Switch createSwitch(
            Vendor vendor,
            Model model,
            IP ip,
            Location location,
            SwitchType switchType
    );

    EdgeRouter addSwitchToEdgeRouter(Switch networkSwitch, EdgeRouter edgeRouter);

    EdgeRouter removeSwitchFromEdgeRouter(Switch networkSwitch, EdgeRouter edgeRouter);
}
