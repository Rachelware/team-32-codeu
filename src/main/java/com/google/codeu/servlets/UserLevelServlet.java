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
        } else if (level == 6) {
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
            //IF CORRECT
            level = level + 1;
            //Increment level
            user.setLevel(level);
            datastore.storeUser(user);
            Gson gson = new Gson();
            String json = gson.toJson(level);

            if(level == 7){
                response.sendRedirect("/escaped.html");
            }
            else{
                response.sendRedirect("/correct.html");
            }
        } else {
            //IF WRONG ANSWER
            if (level != 6) {
                response.sendRedirect("/incorrect.html");
            } else {
                response.sendRedirect("/escaped.html");
            }
        }
    }

    public boolean checkAnswer(String correctAnswer, String userAnswer, int userLevel) throws ArrayIndexOutOfBoundsException {
        boolean isCorrect = false;
        if (userLevel == 1){
            if (Pattern.matches(correctAnswer, userAnswer)){
                isCorrect = true;
            }
        } else if (userLevel == 6) {
            isCorrect = true;
            try {
                String[] userLocs = userAnswer.split("%");
                //Handle the case where input is incorrect
                if (userLocs.length != 3) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                String[] correctLocs = correctAnswer.split("%");
                for (int i = 0; i < 3; i++) {
                    //take away ()
                    userLocs[i] = userLocs[i].replace(')', ' ').replace('(', ' ').trim();
                    correctLocs[i] = correctLocs[i].replace(')', ' ').replace('(', ' ').trim();

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
            } catch (ArrayIndexOutOfBoundsException ex){
                isCorrect = false;
            }
        } else{
            if(correctAnswer.equals(userAnswer)){
                isCorrect = true;
            }
        }
        //System.out.println("Correct? -> " + isCorrect);
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
                result.trim();
                return result;
            }
        }
        result = userAnswer;
        result = result.trim();
        return result;
    }
}
