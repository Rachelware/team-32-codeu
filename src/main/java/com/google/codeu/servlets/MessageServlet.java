/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();
    }

    /**
     * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
     * an empty array if the user is not provided.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        String user = request.getParameter("user");

        if (user == null || user.equals("")) {
            // Request is invalid, return empty array
            response.getWriter().println("[]");
            return;
        }

        List<Message> messages = datastore.getMessages(user);
        Gson gson = new Gson();
        String json = gson.toJson(messages);

        response.getWriter().println(json);
    }

    /** Stores a new {@link Message}. */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }

        String user = userService.getCurrentUser().getEmail();
        int level = datastore.getUser(user).getLevel();
        String text = Jsoup.clean(request.getParameter("text"), Whitelist.none());
        
        /* Can take website links to pictures or gifs in messages*/
        String regex = "(https?://\\S+\\.(png|jpg|gif))";
        String replacement = "<img src=\"$1\" />"; 
        String textWithImagesReplaced = text.replaceAll(regex, replacement);
        
        /* Can take in BBCode to create basic styled text*/

        //Mapped BBCode and HTML replacement to same index of 2 arrays
        String[] BBCodeRegex = {"\\[b\\]((\\S|\\s)+)\\[/b\\]", 
        "\\[i\\]((\\S|\\s)+)\\[/i\\]", 
        "\\[u\\]((\\S|\\s)+)\\[/u\\]", 
        "\\[s\\]((\\S|\\s)+)\\[/s\\]", 
        "\\[quote\\]((\\S|\\s)+)\\[/quote\\]",
        "\\[code\\]((\\S|\\s)+)\\[/code\\]", 
        "\\[size=(?<number>\\d)\\](([^\\[\\]])+)\\[/size\\]",
        "\\[/br\\]"};
        
        String[] BBCodeReplacement = {"<strong>$1</strong>", 
        "<em>$1</em>", 
        "<ins>$1</ins>", 
        "<del>$1</del>", 
        "<q>$1</q>", 
        "<pre>$1</pre>", 
        "<font size=$1>$2</font>",
        "</br>"};
        
        
        //Iterate through to replace each type of tag
        int i;
        String textWithBBCodeReplaced = textWithImagesReplaced;
        for (i = 0; i < BBCodeRegex.length; i++) {
            textWithBBCodeReplaced = textWithBBCodeReplaced.replaceAll(BBCodeRegex[i], BBCodeReplacement[i]);
        }
        
        Message message = new Message(user, textWithBBCodeReplaced, level);
        datastore.storeMessage(message);

        response.sendRedirect("/puzzle.html?user=" + user);
    }
}
