import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;

/**
 * When the user submits the form, Blobstore processes the file upload
 * and then forwards the request to this servlet. This servlet can then
 * process the request using the file URL we get from Blobstore and 
 * analyze the image using the Vision API.
 */
 
 @WebServlet("/image-analysis")
 public class ImageAnalysisServlet extends HttpServlet {
    
    private Datastore datastore;
    private String answer;

    @Override
    public void init() {
        datastore = new Datastore();
        String answer = "none";
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        
        //get the Blobkey that points to the image uploaded by the user
        BlobKey blobKey = getBlobKey(request, "image");
        
        //User didn't upload a file, so render an error message
        if (blobKey == null) {
            out.println("Please upload an image file.");
            return;
        }
        
        //Get the URL of the image that the user uploaded
        String imageUrl = getUploadedFileUrl(blobKey);
        
        //Get the labels of the image that the user uploaded
        byte[] blobBytes = getBlobBytes(blobKey);
        List<EntityAnnotation> imageLabels = getImageLabels(blobBytes);
        
        //check if user is logged in
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }
        
        //get User
        String user = userService.getCurrentUser().getEmail();
        int level = datastore.getUser(user).getLevel(); 
        
        boolean flag = false;
        
        //create image html link and labels string
        String imageHtml = "<a href=\"" + imageUrl + "\">";
        imageHtml = imageHtml + "<img src=\"" + imageUrl + "\" />" + "</a>";
        imageHtml = imageHtml + "<p>Here are the labels we extracted:</p>";
        imageHtml = imageHtml + "<ul>";
        for (EntityAnnotation label : imageLabels) {
            imageHtml = imageHtml + "<li>" + label.getDescription() + " " + label.getScore() + "</li>";
            if (label.getDescription().toUpperCase().equals(datastore.getPuzzle(level).getAnswer())){
                answer = label.getDescription();
                flag = true;
            }
        }
                
        if (flag == false)
            answer = "none";
        imageHtml = imageHtml + "</ul><br/>"+ answer;

        
        //save answer for use in level up
        HttpSession session = request.getSession();
        session.setAttribute("imageAnswer", answer);

        //store image link as a message
        Message myImage = new Message(user, imageHtml, level);
        datastore.storeMessage(myImage);
        response.sendRedirect("/puzzle.html?user=" + user);
    }
    
    /*
    * Returns the BlobKey that points to the file uploaded by the user,
    * or null if the user didn't upload a file.
    */
    private BlobKey getBlobKey(HttpServletRequest request, String formInputElementName){
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get("image");
        
        //User submitted form without selecting a file, so we can't get a BlobKey. (devserver)
        if (blobKeys == null || blobKeys.isEmpty()){
            return null;
        }
        
        //Our form only contains a single file input, so we get the first index.
        BlobKey blobKey = blobKeys.get(0);
        
        //User submitted form without selecting a file, so the BlobKey is empty. (live server)
        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
        if (blobInfo.getSize() == 0) {
            blobstoreService.delete(blobKey);
            return null;
        }
        
        return blobKey;
    }
    
    /* 
    * Blobstore stores files as binary data. This function retrieves the
    * binary data stored at the BlobKey parameter.
    */
    private byte[] getBlobBytes(BlobKey blobKey) throws IOException {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        
        int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
        long currentByteIndex = 0;
        boolean continueReading = true;
        while (continueReading) {
            //end index is inclusive, so we have to subtract 1 to get fetchSize bytes
            byte[] b = blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
            outputBytes.write(b);
            
            //if we read fewer bytes than we requested, then we reached the end
            if (b.length < fetchSize) {
                continueReading = false;
            }
            
            currentByteIndex += fetchSize;
        }
        
        return outputBytes.toByteArray();
    }
    
    /*
    * Uses the Google Cloud Vision API to generate a list of labels that apply to the image
    * represented by the binary data stored in imgBytes.
    */
    private List<EntityAnnotation> getImageLabels(byte[] imgBytes) throws IOException {
        ByteString byteString = ByteString.copyFrom(imgBytes);
        Image image = Image.newBuilder().setContent(byteString).build();
        
        Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);
        
        ImageAnnotatorClient client = ImageAnnotatorClient.create();
        BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
        client.close();
        List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
        AnnotateImageResponse imageResponse = imageResponses.get(0);
        
        if (imageResponse.hasError()){
            System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
            return null;
        }
        
        return imageResponse.getLabelAnnotationsList();
    }
    
    /*
    * Returns a URL that points to the uploaded file
    */
    private String getUploadedFileUrl(BlobKey blobKey) {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
        return imagesService.getServingUrl(options);
    }
    
 }
