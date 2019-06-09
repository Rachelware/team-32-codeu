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

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;


/**
* Handles fetching and saving user data.
*/
@WebServlet("/about")
public class AboutMeServlet extends HttpServlet{
    
    private Datastore datastore;

    @Override
    public void init(){
        datastore = new Datastore();
    }

    /**
    * Responds with the "about me" section for a particular user.
    */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws IOException {
	    
        response.setContentType("text/html");
        
        String user = request.getParameter("user");
        
        if(user == null || user.equals("")) {
            // Request is invalid, return empty response
            return;
        }
        
        User userData = datastore.getUser(user);
        
        if(userData == null || userData.getAboutMe() == null) {
            return;
        }
        
        response.getOutputStream().println(userData.getAboutMe());
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
           
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }
        
        String userEmail = userService.getCurrentUser().getEmail();
        String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.none());
        
        /* Can take website links to pictures or gifs in messages*/
        String regex = "(https?://\\S+\\.(png|jpg|gif))";
        String replacement = "<img src=\"$1\" />"; 
        String aboutMeWithImagesReplaced = aboutMe.replaceAll(regex, replacement);
        
        /* Can take in BBCode to create basic styled text*/

        //Mapped BBCode and HTML replacement to same index of 2 arrays
        String[] BBCodeRegex = {"\\[b\\]((\\S|\\s)+)\\[/b\\]", 
        "\\[i\\]((\\S|\\s)+)\\[/i\\]", 
        "\\[u\\]((\\S|\\s)+)\\[/u\\]", 
        "\\[s\\]((\\S|\\s)+)\\[/s\\]", 
        "\\[quote\\]((\\S|\\s)+)\\[/quote\\]",
        "\\[code\\]((\\S|\\s)+)\\[/code\\]", 
        "\\[size=(?<number>\\d)\\](([^\\[\\]])+)\\[/size\\]"};
        
        String[] BBCodeReplacement = {"<strong>$1</strong>", 
        "<em>$1</em>", 
        "<ins>$1</ins>", 
        "<del>$1</del>", 
        "<q>$1</q>", 
        "<pre>$1</pre>", 
        "<font size=$1>$2</font>"};
        
        
        //Iterate through to replace each type of tag
        int i;
        String aboutMeWithBBCodeReplaced = aboutMeWithImagesReplaced;
        for (i = 0; i < BBCodeRegex.length; i++) {
            aboutMeWithBBCodeReplaced = aboutMeWithBBCodeReplaced.replaceAll(BBCodeRegex[i], BBCodeReplacement[i]);
        }
        
        User user = new User(userEmail, aboutMeWithBBCodeReplaced);
        datastore.storeUser(user);
        
        response.sendRedirect("/user-page.html?user=" + userEmail);
    }
}












