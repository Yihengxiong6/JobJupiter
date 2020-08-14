package db;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation { // create the table we need, only need to build once (or you want to rebuild)
	// Run this as Java application to reset the database.
	public static void main(String[] args) {
		try {
			// Step 1 Connect to MySQL.
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			
			 // The newInstance() call is a work around for some
            // broken Java implementations
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			
			if (conn == null) {
				return;
			}
			
			// Step 2 Drop tables in case they exist.
			Statement statement = conn.createStatement(); // declare a statement to execute 
			String sql = "DROP TABLE IF EXISTS keywords";
			statement.executeUpdate(sql); // it is a deletion operation, so no return 

			sql = "DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);
			
			
			// Step 3 Create new tables
			sql = "CREATE TABLE items (" // plus every column behind
					+ "item_id VARCHAR(255) NOT NULL," // char max is 255
					+ "name VARCHAR(255),"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "PRIMARY KEY (item_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE keywords ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "keyword VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, keyword),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)" 
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					//When you query a TIMESTAMP value, MySQL converts the UTC value back to your connection’s time zone. 
					//Note that this conversion does not take place for other temporal data types.
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," // the time stamp is the last time make query this operation
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			statement.executeUpdate(sql);

			// Step 4: insert fake user 1111/3229c1097c00d497a0fd282d586be050
			sql = "INSERT INTO users VALUES('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')"; // the passwords usually will be Encrypted
			statement.executeUpdate(sql);
			
			
			conn.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
