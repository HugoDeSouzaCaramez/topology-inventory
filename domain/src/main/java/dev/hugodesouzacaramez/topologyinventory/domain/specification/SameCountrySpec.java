package dev.hugodesouzacaramez.topologyinventory.domain.specification;

import dev.hugodesouzacaramez.topologyinventory.domain.entity.CoreRouter;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Equipment;

public final class SameCountrySpec extends AbstractSpecification<Equipment> {

    private final Equipment equipment;

    public SameCountrySpec(Equipment equipment){
        this.equipment = equipment;
    }

    @Override
    public boolean isSatisfiedBy(Equipment anyEquipment) {
        if(anyEquipment instanceof CoreRouter) {
            return true;
        } else if (anyEquipment != null && this.equipment != null) {
            return this.equipment.getLocation().country().
                    equals(anyEquipment.getLocation().country());
        } else{
            return false;
        }
    }

    @Override
    public void check(Equipment equipment) throws GenericSpecificationException {
        if(!isSatisfiedBy(equipment))
            throw new GenericSpecificationException("The equipments should be in the same country");
    }
}
