package dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.RouterManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Router;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.mappers.RouterMapper;
import dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.repository.RouterManagementRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RouterManagementMySQLAdapter implements RouterManagementOutputPort {

    @Inject
    RouterManagementRepository routerManagementRepository;

    @Override
    public Router retrieveRouter(Id id) {
        var routerData = routerManagementRepository.findById(id.getUuid()).subscribe().asCompletionStage().join();
        return RouterMapper.routerDataToDomain(routerData);
    }

    @Override
    public boolean removeRouter(Id id) {
        return routerManagementRepository.deleteById(id.getUuid()).subscribe().asCompletionStage().join();
    }

    @Override
    public Router persistRouter(Router router) {
        var routerData = RouterMapper.routerDomainToData(router);
        Panache.withTransaction(()->routerManagementRepository.persist(routerData));
        return router;
    }
}