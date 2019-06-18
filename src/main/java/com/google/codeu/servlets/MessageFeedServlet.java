package com.google.codeu.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;

/* Handles fetching all messages for the public feed*/
@WebServlet("/feed")
public class MessageFeedServlet extends HttpServlet{

    private Datastore datastore;

    @Override
    public void init(){
        datastore = new Datastore();
    }

    /*Responds with a JSON representation of Message data
      for all users. */
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

    	response.setContentType("application/json");
		UserService userService = UserServiceFactory.getUserService();
		String email = userService.getCurrentUser().getEmail();
		int level;
		if (email == null || email.equals("")) {
			// Request is invalid, return empty array
			response.getWriter().println("[]");
			return;
		} else {
			level = datastore.getUser(email).getLevel();
		}

		List<Message> messages = datastore.getLevelMessages(level);
		Gson gson = new Gson();
		String json = gson.toJson(messages);

		response.getOutputStream().println(json);

    }
}