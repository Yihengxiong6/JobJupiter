package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject obj = new JSONObject();
		if (session != null) {
			MySQLConnection connection = new MySQLConnection();
			String userId = session.getAttribute("user_id").toString();
			obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			connection.close();
		} else {
			obj.put("status", "Invalid Session");
			response.setStatus(403);
		}
		RpcHelper.writeJsonObject(response, obj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * The method will check the input credentials and create a session for a valid user.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        JSONObject input = new JSONObject(IOUtils.toString(request.getReader())); // first transfer to an JSONObject
		String userId = input.getString("user_id"); //getParameter is extract the info directly from the URL
		String password = input.getString("password");
//		// as we know there r two kinds of requests: in the body; in the URL.
//		// in this case, if we use getParameter, userID and password should be null;
//		String userId = request.getParameter("user_id");
//		String password = request.getParameter("password");
		System.out.println("userId:"+ userId);
		System.out.println("password:"+ password);
		MySQLConnection connection = new MySQLConnection();
		JSONObject obj = new JSONObject();
		if (connection.verifyLogin(userId, password)) {
			HttpSession session = request.getSession();// create the session
			session.setAttribute("user_id", userId); // ?????????????????????
			session.setMaxInactiveInterval(600);
			obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
		} else {
			obj.put("status", "Login failed, user id and passcode do not exist.");
			response.setStatus(401);
		}
		connection.close();
		RpcHelper.writeJsonObject(response, obj); // where is session id??????????
	}

}
