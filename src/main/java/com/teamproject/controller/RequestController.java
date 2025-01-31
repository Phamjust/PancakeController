package com.teamproject.controller;

import java.sql.SQLException;

import com.teamproject.dao.AuthenticationDAO;
import com.teamproject.dao.RequestDAO;
import com.teamproject.service.AuthenticationService;
import com.teamproject.service.RequestService;
import com.teamproject.util.Prometheus;

import io.javalin.http.Context;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class RequestController implements AuthenticationService{

	public RequestController() {
		super();
	}
	static PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
	
	//-----------------------------------login
	public void login(Context ctx) {
		Prometheus prom = new Prometheus();
		prom.counter();	//updates prometheus for login attempts
		AuthenticationDAO authDao = new AuthenticationDAO();
		String username = ctx.formParam("username");
		String password = ctx.formParam("password");
		
			if(authDao.authenticateUser(username, password)) {
				ctx.sessionAttribute("username", username);
				ctx.sessionAttribute("password", password);
				
				ctx.status(201);
				} else {
					ctx.status(403);
				}
	}

	
	//-----------------------------------------------customer functions
	public void getAcct(Context ctx){                          //gets account info
		
		String user = ctx.formParam("username");
		String check = ctx.cachedSessionAttribute("username");
		
		if(user.equalsIgnoreCase(check)) {

			 RequestDAO req = new RequestDAO();
			 
			 try {
				req.getAccount(ctx, check);
				ctx.status(200);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			 
		}
	}
	
	public void custDeposit(Context ctx) {       //for deposits
		
		String user = ctx.formParam("username");
		String check = ctx.cachedSessionAttribute("username").toString(); 
		
		if(user.equalsIgnoreCase(check)) {

			 RequestDAO req = new RequestDAO();
			 
			 try {
				req.deposit(ctx, check);
				ctx.status(200);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			 
		}
	}
	
	public void custWithdraw(Context ctx) {  //for withdrawals
		
		String user = ctx.formParam("username");
		String check = ctx.cachedSessionAttribute("username");
		
		if(user.equalsIgnoreCase(check)) {

			 RequestDAO req = new RequestDAO();
			 
			 req.withdraw(ctx, check);
			ctx.status(200);
			 
		}
		
		
	}
	
	public void custTransfer(Context ctx) throws SQLException {  //for transfering between accounts (based on account number)
		
		String user = ctx.formParam("username");
		String check = ctx.cachedSessionAttribute("username");
		int acctNum = Integer.parseInt(ctx.formParam("acctnum"));
		
		if(user.equalsIgnoreCase(check)) {

			 RequestDAO req = new RequestDAO();
			 
			 req.transfer(ctx, check, acctNum);
			ctx.status(200);
			 
		}
		
	}
	
	//--------------------------------------------------------------- for creating new accounts
	public void newAcct(Context ctx) throws SQLException {
		String user = ctx.formParam("username");
		String pass = ctx.formParam("password");
		double balance = Double.parseDouble(ctx.formParam("balance"));
		
		RequestDAO req = new RequestDAO();
		
		req.createAcct(ctx, user, pass, balance);
		ctx.status(201);
	}
		


	
}
