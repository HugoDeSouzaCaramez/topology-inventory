package dev.hugodesouzacaramez.topologyinventory.domain.specification;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.Equipment;
import dev.hugodesouzacaramez.topologyinventory.domain.exception.GenericSpecificationException;

public final class SameIpSpec extends AbstractSpecification<Equipment>{

    private final Equipment equipment;

    public SameIpSpec(Equipment equipment){
        this.equipment = equipment;
    }

    @Override
    public boolean isSatisfiedBy(Equipment anyEquipment) {
        return !equipment.getIp().equals(anyEquipment.getIp());
    }

    @Override
    public void check(Equipment equipment) {
        if(!isSatisfiedBy(equipment))
            throw new GenericSpecificationException("It's not possible to attach routers with the same IP");
    }
}
