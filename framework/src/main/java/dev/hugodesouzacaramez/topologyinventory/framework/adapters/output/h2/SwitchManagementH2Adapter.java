package dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.data.SwitchData;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.mappers.RouterH2Mapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

public class SwitchManagementH2Adapter implements SwitchManagementOutputPort {

    @PersistenceContext
    private EntityManager em;

    public SwitchManagementH2Adapter(){
        setUpH2Database();
    }

    @Override
    public Switch retrieveSwitch(Id id) {
        var switchData = em.getReference(SwitchData.class, id.getUuid());
        return RouterH2Mapper.switchDataToDomain(switchData);
    }

    private void setUpH2Database() {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("inventory");
        EntityManager em =
                entityManagerFactory.createEntityManager();
        this.em = em;
    }
}