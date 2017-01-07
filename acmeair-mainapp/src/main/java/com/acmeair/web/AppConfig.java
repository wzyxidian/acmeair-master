package com.acmeair.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Response;

import com.acmeair.config.AcmeAirConfiguration;
import com.acmeair.config.HttpRequest;


@ApplicationPath("/rest/api")
public class AppConfig extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(AcmeAirConfiguration.class));
    }

    @GET
    @Path("/transR")
    @Produces("application/json")
    public Response getDataServiceInfo() {
        String result = HttpRequest.sendGet();
        return Response.ok(result).build();
    }
}
