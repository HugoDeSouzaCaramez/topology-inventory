package dev.hugodesouzacaramez.topologyinventory.framework.adapters.input.generic;

import dev.hugodesouzacaramez.topologyinventory.application.ports.input.NetworkManagementInputPort;
import dev.hugodesouzacaramez.topologyinventory.application.ports.input.SwitchManagementInputPort;
import dev.hugodesouzacaramez.topologyinventory.application.usecases.NetworkManagementUseCase;
import dev.hugodesouzacaramez.topologyinventory.application.usecases.SwitchManagementUseCase;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Network;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.RouterManagementH2Adapter;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.SwitchManagementH2Adapter;

public class NetworkManagementGenericAdapter {

    private SwitchManagementUseCase switchManagementUseCase;
    private NetworkManagementUseCase networkManagementUseCase;

    public NetworkManagementGenericAdapter(){
        setPorts();
    }

    private void setPorts(){
        this.switchManagementUseCase = new SwitchManagementInputPort(SwitchManagementH2Adapter.getInstance());
        this.networkManagementUseCase = new NetworkManagementInputPort(RouterManagementH2Adapter.getInstance());
    }

    /**
     * POST /network/add
     * */
    public Switch addNetworkToSwitch(Network network, Id switchId) {
        Switch networkSwitch = switchManagementUseCase.retrieveSwitch(switchId);
        return networkManagementUseCase.addNetworkToSwitch(network, networkSwitch);
    }

    /**
     * POST /network/remove
     * */
    public Switch removeNetworkFromSwitch(String networkName, Id switchId) {
        Switch networkSwitch = switchManagementUseCase.retrieveSwitch(switchId);
        return networkManagementUseCase.removeNetworkFromSwitch(networkName, networkSwitch);
    }
}
