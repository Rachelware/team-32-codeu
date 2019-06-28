package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Puzzle;
import com.google.codeu.data.User;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/puzzle")
public class PuzzleServlet {
    private Datastore datastore;


    public void init() {
        datastore = new Datastore();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        datastore.savePuzzles();
        UserService userService = UserServiceFactory.getUserService();
        String user_email = userService.getCurrentUser().getEmail();
        User user = datastore.getUser(user_email);
        int level = user.getLevel();
        String puzzle = datastore.getPuzzle(level).getQuestion();

        Gson gson = new Gson();
        String json = gson.toJson(puzzle);
        response.getWriter().println(json);
    }
}
