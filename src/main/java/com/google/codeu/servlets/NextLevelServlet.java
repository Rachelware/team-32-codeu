package com.google.codeu.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import java.lang.*;


@WebServlet("/next-level")
public class NextLevelServlet extends HttpServlet{
    private Datastore datastore;


    public void init() {
        datastore = new Datastore();
    }

    /** Gets called when a user presses the 'Next level' or 'Try again' button - increments the users level
     * then reloads the page! */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserService userService = UserServiceFactory.getUserService();
        String user_email = userService.getCurrentUser().getEmail();
        User user = datastore.getUser(user_email);

        if (user.getLevel() == 6) {
            response.sendRedirect("/puzzle_level6.html?user=" + user_email);
            return;
        } else if (user.getLevel() == 7) {
            user.setLevel(1);
            datastore.storeUser(user);

            response.sendRedirect("/puzzle.html?user=" + user_email);
            return;
        }
        response.sendRedirect("/puzzle.html?user=" + user_email);
    }
}