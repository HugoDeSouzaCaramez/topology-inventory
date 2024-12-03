package dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.repository;

import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.data.SwitchData;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
@WithSession
public class SwitchManagementRepository implements PanacheRepositoryBase<SwitchData, UUID> {

}