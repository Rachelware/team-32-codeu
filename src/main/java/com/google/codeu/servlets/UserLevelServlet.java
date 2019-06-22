package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Stat;
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
        //TODO: Add logic for checking if input is correct or not
        // Only update stat and user_level if user's answer is correct
        UserService userService = UserServiceFactory.getUserService();
        String user_email = userService.getCurrentUser().getEmail();
        User user = datastore.getUser(user_email);
        int level = user.getLevel() + 1;
        //Increment level
        user.setLevel(level);
        //grab time User started puzzle
        long start_time = user.getTimestamp();
        //Update timeStamp to current time
        user.setTimestamp(System.currentTimeMillis());
        // Grab current time
        long end_time = user.getTimestamp();
        //Save info in stat object
        Stat time_stat = new Stat(user_email, Stat.Stat_Type.DURATION, end_time - start_time, level);
        //Store updated user and stat object
        datastore.storeUser(user);
        datastore.storeStat(time_stat);
        Gson gson = new Gson();
        String json = gson.toJson(level);
        /*
        * if answer is wrong:
        * //Increment ATTEMPTS
        * //Might need to handle converting enum types
        *   Stat stat = datastore.getStat(user, Stat.Stat_type.ATTEMPTS, level);
        *   double value = stat.getValue() + 1;
        *   stat.setValue(value);
        *   datastore.storeStat(stat);
        * */
        response.sendRedirect("/puzzle.html?user=" + user_email);
    }
}
