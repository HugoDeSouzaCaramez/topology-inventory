package dev.hugodesouzacaramez.topologyinventory.framework.adapters.input.rest.outputAdapters;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.IP;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Model;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.SwitchType;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Vendor;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.SwitchManagementMySQLAdapter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class SwitchManagementMySQLAdapterTest {

    @InjectMock
    SwitchManagementMySQLAdapter switchManagementMySQLAdapter;

    @Test
    public void testRetrieveSwitch() {
        Switch aSwitch = getSwitch();
        Mockito.when(switchManagementMySQLAdapter.retrieveSwitch(aSwitch.getId())).thenReturn(aSwitch);
        Switch retrievedSwitch = switchManagementMySQLAdapter.retrieveSwitch(aSwitch.getId());
        Assertions.assertSame(aSwitch, retrievedSwitch);

    }

    Switch getSwitch(){
        return Switch.builder()
                .id(Id.withoutId())
                .switchType(SwitchType.LAYER3)
                .switchNetworks(null)
                .ip(IP.fromAddress("10.0.0.1"))
                .model(Model.XYZ0004)
                .vendor(Vendor.CISCO)
                .location(null)
                .build();
    }
}
