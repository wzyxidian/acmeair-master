/*******************************************************************************
 * Copyright (c) 2013 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.acmeair.web;

import com.acmeair.service.CustomerService;
import com.acmeair.service.ServiceLocator;
import com.acmeair.web.dto.CustomerInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

@Path("/customer")
public class CustomerREST {

	private static int poolSize = 8;
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 200, TimeUnit.MILLISECONDS, new QueueTest<Runnable>(200));
	static int index=0;
	static int count=0;
	public static Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

	private CustomerService customerService = ServiceLocator.instance().getService(CustomerService.class);

	@Context
	private HttpServletRequest request;

	private boolean validate(String customerid)	{
		String loginUser = (String) request.getAttribute(RESTCookieSessionFilter.LOGIN_USER);
		if(logger.isLoggable(Level.FINE)){
			logger.fine("validate : loginUser " + loginUser + " customerid " + customerid);
		}
		return customerid.equals(loginUser);
	}

	protected Logger logger =  Logger.getLogger(CustomerService.class.getName());

	@GET
	@Path("/byid/{custid}")
	@Produces("text/plain")
	public void getCustomer(@CookieParam("sessionid") String sessionid, @PathParam("custid") String customerid, @QueryParam("sendtime") String sendtime,@QueryParam("username") String username) {

//		MyTask myTask = new MyTask(index++,sessionid,customerid,sendtime,username);
//		System.out.println(System.currentTimeMillis()+"start task: "+index);
//		executor.execute(myTask);
//		System.out.println("poolSize: "+executor.getPoolSize()+" , queueWaitSize: "+
//				executor.getQueue().size());
		String[] s = CollectInfo.collectionConfigs();
		for(int i = 0; i < s.length; i++){
			System.out.println(s[i]+"====================");
		}

	}

	class MyTask implements Runnable {
		private int taskNum;
		private String sessionid;
		private String customerid;
		private String sendtime;
		private String username;
		private int ti = 100;
		private int nr = 5000;
		private int z = 20000;
		private int to = 100;

		public MyTask(int num,String sessionid,String customerid,String sendtime,String username) {
			this.taskNum = num;
			this.sessionid=sessionid;
			this.customerid=customerid;
			this.sendtime=sendtime;
			this.username=username;
		}

		@Override
		public void run() {
			try {
				getInfo(sessionid,customerid,sendtime,username);
				count++;
				System.out.println(count);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if(count==index){
					try {
						String line = System.getProperty("line.separator");
						StringBuffer str = new StringBuffer();
						FileWriter fw = new FileWriter("/home/" + System.currentTimeMillis()
								+ ".txt", true);
						for (Entry<String, ArrayList<String>> vo : map.entrySet()) {
							str.append(vo.getKey() + " : ").append(line);
							for (int j = 0; j < vo.getValue().size(); j++) {
								str.append(vo.getValue().get(j) + " ").append(line);
							}
						}
						fw.write(str.toString());
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void getInfo(String sessionid,String customerid,String sendtime,String username){
			System.out.println("send time: " + sendtime);
			if (map.containsKey("Task" + Integer.toString(taskNum))) {
				ArrayList<String> value = map.get("Task"
						+ Integer.toString(taskNum));
				value.add("t0 = " + Long.parseLong(sendtime));
				int num = sendtime.getBytes().length;
				value.add("t0 msi = " + num);
			} else {
				ArrayList<String> value = new ArrayList<String>();
				value.add("t0 = " + Long.parseLong(sendtime));
				int num = sendtime.getBytes().length + username.getBytes().length;
				value.add("t0 msi = " + num);
				map.put("Task" + Integer.toString(taskNum), value);
			}
			if(logger.isLoggable(Level.FINE)){
				logger.fine("getCustomer : session ID " + sessionid + " userid " + customerid);
			}
			try {
				// make sure the user isn't trying to update a customer other than the one currently logged in
				if (!validate(customerid)) {
					System.out.println("error");
				}

				if (map.containsKey("Task" + Integer.toString(taskNum))) {

					ArrayList<String> value = map.get("Task"
							+ Integer.toString(taskNum));
					value.add("t3 = " + System.currentTimeMillis());
					value.add("t3 Cu = 3333333");
					value.add("t3 Ru = 66666666666");
					value.add("t3 f(p) = 100000");
					value.add("t3 ti = " + ti);
					value.add("t3 nr = " + nr);
					value.add("t3 z = " + z);

				} else {

					ArrayList<String> value = new ArrayList<String>();
					value.add("t3 = " + System.currentTimeMillis());
					value.add("t3 Cu = 3333333");
					value.add("t3 Ru = 66666666666");
					value.add("t3 f(p) = 100000");
					value.add("t3 ti = " + ti);
					value.add("t3 nr = " + nr);
					value.add("t3 z = " + z);
					map.put("Task" + Integer.toString(taskNum), value);
				}
				String[] customerIds = username.split(";");
				String ss = customerService.getCustomersByUsernames(customerIds);

				if (map.containsKey("Task" + Integer.toString(taskNum))) {

					ArrayList<String> value = map.get("Task"
							+ Integer.toString(taskNum));
					value.add("t4 = " + System.currentTimeMillis());
					int num = ss.getBytes().length;
					value.add("t4 mso = " + num);
					value.add("t4 to = " + to);

				} else {

					ArrayList<String> value = new ArrayList<String>();
					value.add("t4 = " + System.currentTimeMillis());
					int num = ss.getBytes().length;
					value.add("t4 mso = " + num);
					value.add("t4 to = " + to);
					map.put("Task" + Integer.toString(taskNum), value);
				}
				System.out.println(customerService.getCustomersByUsernames(customerIds));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String toString() {
			return "Task" + taskNum;
		}
	}



	@POST
	@Path("/byid/{custid}")
	@Produces("text/plain")
	public Response putCustomer(@CookieParam("sessionid") String sessionid, CustomerInfo customer) {
		String username = customer.getUsername();
		if (!validate(username)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		String customerFromDB = customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
		if(logger.isLoggable(Level.FINE)){
			logger.fine("putCustomer : " + customerFromDB);
		}

		if (customerFromDB == null) {
			// either the customer doesn't exist or the password is wrong
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		customerService.updateCustomer(username, customer);

		//Retrieve the latest results
		customerFromDB = customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
		return Response.ok(customerFromDB).build();
	}

	@POST
	@Path("/validateid")
	@Consumes({"application/x-www-form-urlencoded"})
	@Produces("text/plain")
	public Response validateCustomer(@FormParam("login") String login, @FormParam("password") String password) {
		if(logger.isLoggable(Level.FINE)){
			logger.fine("validateid : login " + login + " password " + password);
		}

		String validCustomer = null;

		if (customerService.validateCustomer(login, password)) {
			validCustomer = "true";
		} else {
			validCustomer = "false";
		}

		String s = "{\"validCustomer\":\"" + validCustomer + "\"}";

		return Response.ok(s).build();
	}



}
