package dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.mappers.RouterMapper;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.repository.SwitchManagementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SwitchManagementMySQLAdapter implements SwitchManagementOutputPort {

    @Inject
    SwitchManagementRepository switchManagementRepository;

    @Override
    public Switch retrieveSwitch(Id id) {
        var switchData = switchManagementRepository.findById(id.getUuid()).subscribe().asCompletionStage().join();
        return RouterMapper.switchDataToDomain(switchData);
    }
}
