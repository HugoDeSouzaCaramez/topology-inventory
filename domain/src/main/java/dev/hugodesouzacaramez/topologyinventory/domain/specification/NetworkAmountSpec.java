package dev.hugodesouzacaramez.topologyinventory.domain.specification;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.Equipment;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.exception.GenericSpecificationException;

public final class NetworkAmountSpec extends AbstractSpecification<Equipment> {

    final static public int MAXIMUM_ALLOWED_NETWORKS = 6;

    @Override
    public boolean isSatisfiedBy(Equipment switchNetwork) {
        return ((Switch)switchNetwork).getSwitchNetworks().size()
                <=MAXIMUM_ALLOWED_NETWORKS;
    }

    @Override
    public void check(Equipment equipment) throws GenericSpecificationException {
        if(!isSatisfiedBy(equipment))
            throw new GenericSpecificationException("The max number of networks is "+ NetworkAmountSpec.MAXIMUM_ALLOWED_NETWORKS);
    }
}
