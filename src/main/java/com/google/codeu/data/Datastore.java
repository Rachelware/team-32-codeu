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

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.*;
import com.google.appengine.api.datastore.FetchOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Provides access to the data stored in Datastore. */
public class Datastore {

    private DatastoreService datastore;


    public Datastore() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void savePuzzles() {
        Puzzle puzzle1 = new Puzzle(1, "(ELEPHANT'?S?)?\\s?SHADOW");
        storePuzzle(puzzle1);
        Puzzle puzzle2 = new Puzzle(2, "CHEETAH");
        storePuzzle(puzzle2);
        Puzzle puzzle3 = new Puzzle(3, "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG");
        storePuzzle(puzzle3);
        Puzzle puzzle4 = new Puzzle(4, "CANNOLI");
        storePuzzle(puzzle4);
        Puzzle puzzle5 = new Puzzle(5, "(47.6038321,-122.3300623)");
        storePuzzle(puzzle5);
        Puzzle puzzle6 = new Puzzle(6, "38, -121%51, 30%10, -75");
        storePuzzle(puzzle6);
    }

    /** Stores the Message in Datastore. */
    public void storeMessage(Message message) {
        Entity messageEntity = new Entity("Message", message.getId().toString());
        messageEntity.setProperty("user", message.getUser());
        messageEntity.setProperty("level", message.getLevel());
        messageEntity.setProperty("text", message.getText());
        messageEntity.setProperty("timestamp", message.getTimestamp());
        datastore.put(messageEntity);
    }

    /**
     * Gets messages posted by a specific user.
     *
     * @return a list of messages posted by the user, or empty list if user has never posted a
     *     message. List is sorted by time descending.
     */
    public List<Message> getMessages(String user) {
        List<Message> messages = new ArrayList<>();
        Query query =
            new Query("Message").setFilter(new Query
                    .FilterPredicate("user", FilterOperator.EQUAL, user))
                    .addSort("timestamp", SortDirection.ASCENDING);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            try {
                Message message = messageQuery(user, entity);
                messages.add(message);
            } catch (Exception e) {
                printError(entity, e);
            }
        }
        return messages;
    }

    public Set<String> getUsers(){
        Set<String> users = new HashSet<>();
        Query query = new Query("Message");
        PreparedQuery results = datastore.prepare(query);
        for(Entity entity : results.asIterable()) {
            users.add((String) entity.getProperty("user"));
        }
        return users;
    }

    /** Returns the total number of messages for all users. */
    public int getTotalMessageCount(){
        Query query = new Query("Message");
        PreparedQuery results = datastore.prepare(query);
        return results.countEntities(FetchOptions.Builder.withLimit(1000));
    }


    /** Stores the User in Datastore. */
    public void storeUser(User user) {
        Entity userEntity = new Entity("User", user.getEmail());
        userEntity.setProperty("email", user.getEmail());
        userEntity.setProperty("time", user.getTimestamp());
        userEntity.setProperty("aboutMe", user.getAboutMe());
        userEntity.setProperty("level", user.getLevel());
        datastore.put(userEntity);
    }

    /** Stores the Puzzle in Datastore. */
    public void storePuzzle(Puzzle puzzle) {
        Entity puzzleEntity = new Entity("Puzzle", puzzle.getLevel());
        puzzleEntity.setProperty("answer", puzzle.getAnswer());
        puzzleEntity.setProperty("level", puzzle.getLevel());
        datastore.put(puzzleEntity);
    }

    /**
     * Returns the User owned by the email address, or
     * null if no matching User was found.
     */
    public Puzzle getPuzzle(int level) {
        savePuzzles();
        Query query = new Query("Puzzle").setFilter(new Query.FilterPredicate("level", FilterOperator.EQUAL, level));
        PreparedQuery results = datastore.prepare(query);
        Entity puzzleEntity = results.asSingleEntity();
        if(puzzleEntity == null) {
            return null;
        }
        String answer = (String) puzzleEntity.getProperty("answer");
        Puzzle puzzle = new Puzzle(level, answer);
        return puzzle;
    }

    /**
    * Returns the User owned by the email address, or
    * null if no matching User was found.
    */
    public User getUser(String email) {
        Query query = new Query("User").setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
        PreparedQuery results = datastore.prepare(query);
        Entity userEntity = results.asSingleEntity();
        if(userEntity == null) {
            return null;
        }
        String aboutMe = (String) userEntity.getProperty("aboutMe");
        long level = (long) userEntity.getProperty("level");
        //Trying to handle NPE
        long timeStamp;
        if (userEntity.getProperty("time") == null) {
            timeStamp = System.currentTimeMillis();
        } else {
            timeStamp = (long) userEntity.getProperty("time");
        }
        User user = new User(email, aboutMe, (int) level);
        user.setTimestamp(timeStamp);
        return user;
    }

  /**
   * Gets messages for all users
   *
   * @return a list of messages from all the users.
   * List stored in time descending order.
   **/

    public List<Message> getAllMessages(){
        List<Message> messages = new ArrayList<>();
        Query query = new Query("Message")
            .addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()){
            try {
                String user = (String) entity.getProperty("user");
                Message message = messageQuery(user, entity);
                messages.add(message);
            } catch (Exception e){
                printError(entity, e);
            }
        }
        return messages;
    }

    public List<Message> getLevelMessages(int level, long timestamp){
        List<Message> messages = new ArrayList<>();
        Query.Filter levelFilter = new Query.FilterPredicate("level", FilterOperator.EQUAL, level);
        Query.Filter timeFilter = new Query.FilterPredicate("timestamp", FilterOperator.LESS_THAN_OR_EQUAL, timestamp);
        Query query = new Query("Message").setFilter(new Query.FilterPredicate("level", FilterOperator.EQUAL, level)).setFilter(new Query.FilterPredicate("timestamp", FilterOperator.GREATER_THAN, timestamp))
                .addSort("timestamp", SortDirection.ASCENDING);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()){
            try {
                String user = (String) entity.getProperty("user");
                Message message = messageQuery(user, entity);
                messages.add(message);
            } catch (Exception e){
                printError(entity, e);
            }
        }
        return messages;
    }

    public Message messageQuery(String user, Entity newEntity){
        String idString = newEntity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String text = (String) newEntity.getProperty("text");
        long timestamp = (long) newEntity.getProperty("timestamp");
        int level = getUser(user).getLevel();
        Message message = new Message(id, user, text, timestamp, level);
        return message;
    }

    public void printError(Entity newEntity, Exception e){
        System.err.println("Error reading message. ");
        System.err.println(newEntity.toString());
        e.printStackTrace();
    }

}
