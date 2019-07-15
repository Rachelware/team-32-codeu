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
import com.google.codeu.data.Stat;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import java.lang.*;
import java.util.regex.*;

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
        int level = user.getLevel();
        String answer;
        
        
        if (level == 2) { //for image input, get answer from ImageAnalysisServlet
            HttpSession session = request.getSession();
            answer = (String) session.getAttribute("imageAnswer");
            System.out.print("ANSWER: " + answer);
        }
        else {
            answer = Jsoup.clean(request.getParameter("answer"), Whitelist.none());
            answer = stripAnswer(answer.toUpperCase());
        }

        String correct_answer = datastore.getPuzzle(level).getAnswer();
      
        if (checkAnswer(correct_answer, answer, level)) {
            //If the user's input is correct
            level = level + 1;
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
            response.sendRedirect("/puzzle.html?user=" + user_email);
        } else {

            Stat attempt_stat = datastore.getStat(user_email, Stat.Stat_Type.ATTEMPTS, level);
            if (attempt_stat == null){
                attempt_stat = new Stat(user_email, Stat.Stat_Type.ATTEMPTS, 0, level);
            }
            attempt_stat.incrementValue();
            datastore.storeStat(attempt_stat);
            response.sendRedirect("/puzzle.html?user=" + user_email);
        }
    }

    public boolean checkAnswer(String correctAnswer, String userAnswer, int userLevel){
        boolean isCorrect = false;
        if(userLevel == 1){
            if(Pattern.matches(correctAnswer, userAnswer)){
                isCorrect = true;
                System.out.println("Correct answer: " + correctAnswer);
                System.out.println("User answer: " + userAnswer);
            }
        }
        else{
            if(correctAnswer.equals(userAnswer)){
                isCorrect = true;
            }
        }
        return isCorrect;
    }

    /*strips the user answer of any "an" "a" or "the" 
    assumes you pass in answer in all caps
    */
    public String stripAnswer(String userAnswer){
        String result = "";
        if(userAnswer.indexOf(' ') != -1){
            String firstWord = userAnswer.substring(0, userAnswer.indexOf(' '));

            //if the first word of the user answer equals an, a, or the, remove from string
            if(firstWord.equals("AN") || firstWord.equals("A") || firstWord.equals("THE")){
                result = userAnswer.substring(userAnswer.indexOf(' ') + 1);
            }
        }
        
        result = result.trim();
        return result;
    }
}
