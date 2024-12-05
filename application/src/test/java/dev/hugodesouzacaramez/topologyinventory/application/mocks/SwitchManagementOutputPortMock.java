package dev.hugodesouzacaramez.topologyinventory.application.mocks;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import io.quarkus.test.Mock;

@Mock
public class SwitchManagementOutputPortMock implements SwitchManagementOutputPort {
    @Override
    public Switch retrieveSwitch(Id id) {
        return null;
    }
}
