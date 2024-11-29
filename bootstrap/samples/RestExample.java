package dev.hugodesouzacaramez.topologyinventory.bootstrap.samples;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/app")
public class    RestExample {

    @Inject
    BeanExample beanExample;

    @Inject
    PersistenceExample persistenceExample;

    @GET
    @Path("/simple-rest")
    @Produces(MediaType.TEXT_PLAIN)
    public String simpleRest() {
        return "This REST endpoint is provided by Quarkus";
    }

    @GET
    @Path("/simple-bean")
    @Produces(MediaType.TEXT_PLAIN)
    public String simpleBean() {
        return beanExample.simpleBean();
    }
}