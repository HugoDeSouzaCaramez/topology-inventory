package dev.hugodesouzacaramez.topologyinventory.application.usecases;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.IP;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Network;

public interface NetworkManagementUseCase {

    Network createNetwork(
            IP networkAddress,
            String networkName,
            int networkCidr);

    Switch addNetworkToSwitch(Network network, Switch networkSwitch);

    Switch removeNetworkFromSwitch(String name, Switch networkSwitch);
}