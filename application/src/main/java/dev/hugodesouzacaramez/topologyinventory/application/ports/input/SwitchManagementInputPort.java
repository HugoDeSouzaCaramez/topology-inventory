package dev.hugodesouzacaramez.topologyinventory.application.ports.input;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.application.usecases.SwitchManagementUseCase;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.EdgeRouter;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SwitchManagementInputPort implements SwitchManagementUseCase {

    private SwitchManagementOutputPort switchManagementOutputPort;

    @Override
    public void setOutputPort(SwitchManagementOutputPort switchManagementOutputPort) {
        this.switchManagementOutputPort = switchManagementOutputPort;
    }

    @Override
    public Switch createSwitch(
            Vendor vendor,
            Model model,
            IP ip,
            Location location,
            SwitchType switchType) {
        return Switch
                .builder()
                .id(Id.withoutId())
                .vendor(vendor)
                .model(model)
                .ip(ip)
                .location(location)
                .switchType(switchType)
                .build();
    }

    public Switch retrieveSwitch(Id id) {
        return switchManagementOutputPort.retrieveSwitch(id);
    }

    @Override
    public EdgeRouter addSwitchToEdgeRouter(Switch networkSwitch, EdgeRouter edgeRouter) {
        networkSwitch.setRouterId(edgeRouter.getId());
        edgeRouter.addSwitch(networkSwitch);
        return edgeRouter;
    }

    @Override
    public EdgeRouter removeSwitchFromEdgeRouter(Switch networkSwitch, EdgeRouter edgeRouter) {
        edgeRouter.removeSwitch(networkSwitch);
        return edgeRouter;
    }
}