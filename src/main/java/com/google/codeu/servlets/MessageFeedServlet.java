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
import com.google.codeu.data.User;
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
        long timestamp;
        User user;
        if (datastore.getUser(email) == null) {
            user = new User(email, null, 1);
            datastore.storeUser(user);
            level = user.getLevel();
            timestamp = user.getTimestamp();
        } else {
            user = datastore.getUser(email);
            level = user.getLevel();
            timestamp = user.getTimestamp();
        }
        List<Message> messages = datastore.getLevelMessages(level, timestamp);
        Gson gson = new Gson();
        String json = gson.toJson(messages);
        response.getOutputStream().println(json);
    }
}