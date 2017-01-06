package com.acmeair.web;

import com.acmeair.config.AcmeAirConfiguration;
import com.acmeair.service.ServiceLocator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/6.
 */
@ApplicationPath("/rest/trans")
public class TransReq{
    @GET
    @Path("/transRequest")
    @Produces("application/json")
    public String getDataServiceInfo() {
        return "hello0";
    }
}
