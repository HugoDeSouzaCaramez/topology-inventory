package dev.hugodesouzacaramez.topologyinventory.framework.adapters.input.rest.converter;

import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;

import jakarta.ws.rs.ext.ParamConverter;

public class IdParamConverter implements ParamConverter<Id> {

    @Override
    public Id fromString(String value){
       return Id.withId(value);
    }

    @Override
    public String toString(Id id) {
        return id.getUuid().toString();
    }
}
