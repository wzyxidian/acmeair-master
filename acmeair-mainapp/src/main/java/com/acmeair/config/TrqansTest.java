package com.acmeair.config;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by Administrator on 2017/1/6.
 */
@Path("/configs")
public class TrqansTest {
    @GET
    @Path("/transR")
    @Produces("application/json")
    public Response getDataServiceInfo(@CookieParam("sessionid") String sessionid) {
        String result = HttpRequest.sendGet(sessionid);
        return Response.ok(result).build();
    }
}
