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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.acmeair.service.CustomerService;
import com.acmeair.service.ServiceLocator;
import com.acmeair.web.dto.CustomerInfo;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Path("/customer")
public class CustomerREST {

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(6, 6, 200, TimeUnit.MILLISECONDS, new QueueTest<Runnable>(7));
	private static int c;
	static int index=0;

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
	public void getCustomer(@CookieParam("sessionid") String sessionid, @PathParam("custid") String customerid, @QueryParam("sendtime") String sendtime) {

		MyTask myTask = new MyTask(index++,sessionid,customerid,sendtime);
		System.out.println(System.currentTimeMillis()+"开始执行task"+index);
		executor.execute(myTask);
		System.out.println("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+
				executor.getQueue().size()+"，已执行玩别的任务数目："+executor.getCompletedTaskCount());

	}

	class MyTask implements Runnable {
		private int taskNum;
		private String sessionid;
		private String customerid;
		private String sendtime;

		public MyTask(int num,String sessionid,String customerid,String sendtime) {
			this.taskNum = num;
			this.sessionid=sessionid;
			this.customerid=customerid;
			this.sendtime=sendtime;
		}

		@Override
		public void run() {
			System.out.println("正在执行task "+taskNum);
			try {
				getInfo(sessionid,customerid,sendtime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("task "+taskNum+"执行完毕");
		}

		public void getInfo(String sessionid,String customerid,String sendtime){
			System.out.println("faqongqingqiushijian:" + sendtime);
			if(logger.isLoggable(Level.FINE)){
				logger.fine("getCustomer : session ID " + sessionid + " userid " + customerid);
			}
			try {
				// make sure the user isn't trying to update a customer other than the one currently logged in
				if (!validate(customerid)) {
					System.out.println("error");
				}
				System.out.println(customerService.getCustomerByUsername(customerid));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
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
