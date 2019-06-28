package com.google.codeu.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.IOException;
import java.util.List;

import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;

/**
 * When the user submits the form, Blobstore processes the file upload
 * and then forwards the request to this servlet. This servlet can then
 * process the request using the file URL we get from Blobstore.
 */
 @WebServlet("/form-handler")
 public class FormHandlerServlet extends HttpServlet{
    
    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        //get the Url of the image uploaded to blobstore
        String imageUrl = getUploadedFileUrl(request, "image");
        
        //connected version (does not work, waiting for other changes to be merged)
        /**
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }
        
        //get User
        String user = userService.getCurrentUser().getEmail();
        int level = datastore.getUser(user).getLevel(); **/
        
        /**
        String imageHtml = "<a href=\"" + imageUrl + "\">" + "<img src=\"" + imageUrl + "\" />" + "</a>";
        
        Message myImage = new Message(user, imageHtml, level);
        datastore.storeMessage(myImage);
        
        response.sendRedirect("imageUpload.html"); **/
        
        //non-connected version
        // Output some HTML that shows the data the user entered.
        // A real codebase would probably store these in Datastore.
        ServletOutputStream out = response.getOutputStream();
        out.println("<p>Here's the image you uploaded:</p>");
        out.println("<a href=\"" + imageUrl + "\">");
        out.println("<img src=\"" + imageUrl + "\" />");
        out.println("</a>");
        
    }
    
    //Returns a URL that points to the uploaded file, or null if the user didn't upload a file.
    private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName){
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get("image");
        
        //User submitted form without selecting a file, so we can't get a URL. (devserver)
        if(blobKeys == null || blobKeys.isEmpty()){
            return null;
        }
        
        //get first index because there is one file
        BlobKey blobKey = blobKeys.get(0);
        
        //User submitted without selecting a file, so we can't get a URL. (live server)
        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
        if(blobInfo.getSize() == 0){
            blobstoreService.delete(blobKey);
            return null;
        }
        
        //could check validity of file, make sure it is an image here
        
        //Use ImagesService to get a URL that point to the uploaded file.
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
        return imagesService.getServingUrl(options);
    }
 }