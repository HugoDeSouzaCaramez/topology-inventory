package dev.hugodesouzacaramez.topologyinventory.application.usecases;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.EdgeRouter;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.*;

public interface SwitchManagementUseCase {

    Switch createSwitch(
            Vendor vendor,
            Model model,
            IP ip,
            Location location,
            SwitchType switchType
    );

    Switch retrieveSwitch(Id id);

    EdgeRouter addSwitchToEdgeRouter(Switch networkSwitch, EdgeRouter edgeRouter);

    EdgeRouter removeSwitchFromEdgeRouter(Switch networkSwitch, EdgeRouter edgeRouter);
}
