package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import com.google.gson.Gson;

@WebServlet("/user-level")
public class UserLevelServlet extends HttpServlet{
    private Datastore datastore;


    public void init() {
        datastore = new Datastore();
    }

    /** Grabs a users level to display */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        UserService userService = UserServiceFactory.getUserService();
        String user_email = userService.getCurrentUser().getEmail();
        User user = datastore.getUser(user_email);
        if (user == null) {
            user = new User(user_email, null, 1);
            datastore.storeUser(user);
        }
        int level = user.getLevel();
        Gson gson = new Gson();
        String json = gson.toJson(level);
        response.getOutputStream().println(json);
    }

    /** Gets called when a user presses the 'Level Up' button - increments the users level
     * then reloads the page! */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        String user_email = userService.getCurrentUser().getEmail();
        User user = datastore.getUser(user_email);
        int level = user.getLevel() + 1;
        user.setLevel(level);
        datastore.storeUser(user);
        Gson gson = new Gson();
        String json = gson.toJson(level);
        //response.getOutputStream().println(json);
        response.sendRedirect("/user-page.html?user=" + user_email);
    }
}
