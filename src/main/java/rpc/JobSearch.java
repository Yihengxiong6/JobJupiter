package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;
import external.GitHubClient;

/**
 * Servlet implementation class JobSearch
 */
public class JobSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JobSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		MySQLConnection connection = new MySQLConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
		connection.close();

		GitHubClient client = new GitHubClient(); 
		JSONArray array = new JSONArray();
		List<Item> items = client.search(lat, lon, null);
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			obj.put("favorite", favoritedItemIds.contains(item.getItemId())); // if favorite set contains the item, we add favorite to JSONObject
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
		


//		if (request.getParameter("username") != null) {
//			JSONObject obj = new JSONObject();
//			String username = request.getParameter("username");
//			obj.put("username", username);
//			RpcHelper.writeJsonObject(response, obj);
//		}
//		
//		PrintWriter writer = response.getWriter(); // PrintWriter is from the field of response
//		
//		JSONObject obj = new JSONObject();
//		obj.put("username", "yiheng xiong");
//		obj.put("age", "40");
//		writer.print(obj);
//		
		// If want to use an array
		// JSONArray
//		response.setContentType("application/json");
//		PrintWriter writer = response.getWriter();
//		
//		JSONArray array = new JSONArray();
//		array.put(new JSONObject().put("name", "abcd").put("address", "San Francisco").put("time", "01/01/2017"));
//		array.put(new JSONObject().put("name", "1234").put("address", "San Jose").put("time", "01/01/2017"));
//		writer.print(array);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
