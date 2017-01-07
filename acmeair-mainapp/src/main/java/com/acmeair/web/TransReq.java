package com.acmeair.web;

import com.acmeair.config.AcmeAirConfiguration;
import com.acmeair.config.HttpRequest;
import com.acmeair.config.TrqansTest;
import com.acmeair.service.ServiceLocator;

import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2017/1/6.
 */
//@ApplicationPath("/rest/trans")
@Path("/rest/trans")
public class TransReq extends Application{
    /*public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(TrqansTest.class));
    }*/
    @GET
    @Path("/transR")
    @Produces("application/json")
    public Response getDataServiceInfo() {
        String result = HttpRequest.sendGet();
        return Response.ok(result).build();
    }
}
