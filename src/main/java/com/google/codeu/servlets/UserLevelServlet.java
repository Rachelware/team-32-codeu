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

    /** Gets called when a user presses the 'Submit' button - increments the users level
     * then reloads the page! */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserService userService = UserServiceFactory.getUserService();
        String user_email = userService.getCurrentUser().getEmail();
        User user = datastore.getUser(user_email);
        int level = user.getLevel();
        String answer = "";
        
        
        if (level == 2) { //for image input, get answer from ImageAnalysisServlet
            HttpSession session = request.getSession();
            answer = (String) session.getAttribute("imageAnswer");
            if (answer == null) {
                response.sendRedirect("/puzzle.html?user=" + user_email);
                return;
            }
            System.out.print("ANSWER: " + answer);
            answer = answer.toUpperCase();
            session.removeAttribute("imageAnswer");
            session.setAttribute("imageAnswer", "blorp");
            System.out.print("ANSWER: " + answer);
        } else if (level == 4) {
            answer = Jsoup.clean(request.getParameter("answer"), Whitelist.none());
        } else {
            answer = Jsoup.clean(request.getParameter("answer"), Whitelist.none());
            if (level == 1) {
                answer = stripAnswer(answer.toUpperCase());
            } else {
                answer = answer.toUpperCase();
            }
        }

        String correct_answer = datastore.getPuzzle(level).getAnswer();
      
        if (checkAnswer(correct_answer, answer, level)) {
            //If the user's input is correct
            level = level + 1;
            //Increment level
            user.setLevel(level);
            //grab time User started puzzle
            //long start_time = user.getTimestamp();
            //Update timeStamp to current time
            //user.setTimestamp(System.currentTimeMillis());
            // Grab current time
            //long end_time = user.getTimestamp();
            //Save info in stat object
            //Stat time_stat = new Stat(user_email, Stat.Stat_Type.DURATION, end_time - start_time, level);
            //Store updated user and stat object
            datastore.storeUser(user);
            //datastore.storeStat(time_stat);
            Gson gson = new Gson();
            String json = gson.toJson(level);
            if (level != 5) {
                response.sendRedirect("/puzzle.html?user=" + user_email);
            } else {
                response.sendRedirect("/level5.html");
                return;
            }
        } else {

           /* Stat attempt_stat = datastore.getStat(user_email, Stat.Stat_Type.ATTEMPTS, level);
            if (attempt_stat == null){
                attempt_stat = new Stat(user_email, Stat.Stat_Type.ATTEMPTS, 0, level);
            }
            attempt_stat.incrementValue();
            datastore.storeStat(attempt_stat); */
            if (level != 4) {
                response.sendRedirect("/puzzle.html?user=" + user_email);
            } else {
                response.sendRedirect("/level4.html");
            }
        }

    }

    public boolean checkAnswer(String correctAnswer, String userAnswer, int userLevel){
        boolean isCorrect = false;
        if (userLevel == 1){
            if (Pattern.matches(correctAnswer, userAnswer)){
                isCorrect = true;
            }
        } else if (userLevel == 4) {
            isCorrect = true;
            String[] userLocs = userAnswer.split("%");
            String[] correctLocs = correctAnswer.split("%");
            for (int i = 0; i < 3; i++) {
                //take away ()
                userLocs[i] = userLocs[i].replace(')',' ').replace('(',' ').trim();
                correctLocs[i] = correctLocs[i].replace(')',' ').replace('(',' ').trim();

                //split string into two "num1, num2" -> [num1, num2]
                String[] a1 = userLocs[i].split(", ");
                //ignore decimals. 00.0000 -> [00, 0000]
                String[] a2 = a1[0].split("\\.");
                //only grab first index
                String formUserAnswer = a2[0];
                //a2[1]
                a2 = a1[1].split("\\.");
                formUserAnswer += ", " + a2[0];
                userLocs[i] = formUserAnswer;

                //check if answer is correct
                if (!userLocs[i].equals(correctLocs[i])) {
                    isCorrect = false;
                }
            }
        } else{
            if(correctAnswer.equals(userAnswer)){
                isCorrect = true;
            }
        }
        System.out.println("Correct? -> " + isCorrect);
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
