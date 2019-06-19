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

    /** Stores the Message in Datastore. */
    public void storeMessage(Message message) {
        Entity messageEntity = new Entity("Message", message.getId().toString());
        messageEntity.setProperty("user", message.getUser());
        messageEntity.setProperty("text", message.getText());
        messageEntity.setProperty("timestamp", message.getTimestamp());
        datastore.put(messageEntity);
    }

    //Stores Image URL for BLobstore
    public void storeBlobUrl(String imageUrl) {
        Entity imageUrlEntity = new Entity("");
        //use as entity?
        datastore.put(imageUrlEntity);
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
            new Query("Message").setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
            .addSort("timestamp", SortDirection.DESCENDING);
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

    public int getActiveUserCount() {
        List<Object> users = new ArrayList<>();
        Query query = new Query("Message");
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            Object user = entity.getProperty("user");
            if (!users.contains(user)) {
                users.add(user);
            }
        }
        return users.size();
    }

    public String getAverageMessagesPerUser() {
        int users = getActiveUserCount();
        int messages = getTotalMessageCount();
        String average = Float.toString((float) messages / users);
        return average;
    }

    /** Stores the User in Datastore. */
    public void storeUser(User user) {
        Entity userEntity = new Entity("User", user.getEmail());
        userEntity.setProperty("email", user.getEmail());
        userEntity.setProperty("aboutMe", user.getAboutMe());
        datastore.put(userEntity);
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
        User user = new User(email, aboutMe);
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

    public Message messageQuery(String user, Entity newEntity){
        String idString = newEntity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String text = (String) newEntity.getProperty("text");
        long timestamp = (long) newEntity.getProperty("timestamp");
        Message message = new Message(id, user, text, timestamp);
        return message;
    }

    public void printError(Entity newEntity, Exception e){
        System.err.println("Error reading message. ");
        System.err.println(newEntity.toString());
        e.printStackTrace();
    }
}
