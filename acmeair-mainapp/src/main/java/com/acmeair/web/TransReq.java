package com.acmeair.web;

import com.acmeair.config.TrqansTest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/1/6.
 */
@ApplicationPath("/rest/trans")
public class TransReq extends  Application{
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(TrqansTest.class));
    }


}
