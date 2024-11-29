package dev.hugodesouzacaramez.topologyinventory.samples;

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

    @POST
    @Path("/request-validation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Result validation(@Valid SampleObject sampleObject) {
        try {
            return new Result("The request data is valid!");
        } catch (ConstraintViolationException e) {
            return new Result(e.getConstraintViolations());
        }
    }
}