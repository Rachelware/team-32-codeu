@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {

  	/** Responds with a JSON array containing marker data. */
  	@Override
  	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	response.setContentType("application/json");

    	List<Marker> markers = getMarkers();
    	Gson gson = new Gson();
    	String json = gson.toJson(markers);

    	response.getOutputStream().println(json);
  }